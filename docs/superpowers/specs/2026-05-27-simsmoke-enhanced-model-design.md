# SimSmoke Enhanced Aggregate Model — Design Spec

## Problem

The current simulator produces flat/parallel lines because it uses linear interpolation to a pre-computed target prevalence. After year 5 (convergence), all values are identical every year. Deaths avoided are a static snapshot, not cumulative. This makes the simulator's output visually meaningless and epidemiologically incorrect.

## Goal

Replace the static interpolation engine with a year-by-year dynamic simulation that produces realistic diverging curves: prevalence declining gradually, deaths avoided growing cumulatively. Backwards-compatible API. No age/sex stratification yet (that's Phase 2).

## Approach: Enhanced Aggregate Model (Phase 1)

### Algorithm — Year-by-Year Loop

For each year `t` from 1 to `horizonte`:

**1. Natural trend (baseline decline)**
Both the "no intervention" baseline and the "with intervention" line decline naturally:
```
tendencia = SIM_TENDENCIA_NATURAL_PP_ANUAL  // -0.34 pp/year (ENCODAT 2016→2025)
prevalenciaBaseline(t) = prevalenciaBaseline(t-1) + tendencia
```
The baseline line slopes downward even without policies. Floor at 0.5%.

**2. Policy effects (first year: prevalence shift, subsequent years: cessation/initiation)**
- Year 1: Apply `efecto_prevalencia_pct` multiplicatively (existing logic, one-time shift)
- Years 2+: Apply `efecto_cesacion_pct` as ongoing annual cessation boost and `efecto_inicio_pct` as ongoing initiation reduction, with exponential adoption curve:
```
adopcion(t) = 1 - e^(-t / tau)    // tau = 2.5, most effect in years 1-3
efectoOngoing(t) = (cesacionTotal + inicioTotal) * adopcion(t) / 100
prevalenciaIntervencion(t) = prevalenciaBaseline(t) - efectoPrimerAnio - efectoOngoing(t)
```

**3. Tax/price elasticity (age-weighted)**
Instead of averaging all elasticities, weight by approximate smoking population share per age group:
```
Weights (from ENCODAT): 15-17: 5%, 18-24: 20%, 25-34: 25%, 35-44: 25%, 45+: 25%
efectoPrecio = Σ(elasticidad_i * peso_i) * incrementoPrecioPct
```
Applied once in year 1 as a prevalence multiplier (same as current, but weighted).

**4. Dynamic population**
```
poblacion(t) = SUM(pob_proyecciones WHERE anio = anioBase + t, national level, 18+ age groups)
fumadores(t) = poblacion(t) * prevalencia(t) / 100
```
Falls back to static population if projection data not available for that year.

**5. Deaths avoided (cumulative, growing)**
```
tasaMortalidadAtribuible = defuncionesAtribuiblesBase / fumadoresBase
muertesBaseline(t) = fumadoresBaseline(t) * tasaMortalidadAtribuible
muertesIntervencion(t) = fumadoresIntervencion(t) * tasaMortalidadAtribuible
evitadasAnuales(t) = muertesBaseline(t) - muertesIntervencion(t)
evitadasAcumuladas(t) = evitadasAcumuladas(t-1) + evitadasAnuales(t)
```
This produces a growing cumulative curve — the signature of a functioning simulation.

### Expected Visual Output

- **Prevalence chart**: Two lines that both slope downward. Baseline declines slowly (natural trend). Intervention line drops faster and diverges from baseline. The gap widens over time.
- **Deaths avoided chart**: Upward-sloping cumulative curve that grows each year.

## Backend Changes

### `SimulacionService.java`
- Rewrite `proyectar()` method with the year-by-year algorithm above
- Add `calcularEfectoPreciosPonderado()` using age-weighted elasticities
- Add baseline projection (without intervention) alongside intervention projection
- Remove `ANIOS_CONVERGENCIA` constant and linear interpolation logic

### `SimulacionRepository.java`
- Add `obtenerPoblacionNacional(int anio)`: query `pob_proyecciones` summing 18+ age groups nationally for a given year
- Modify `obtenerPoliticas()` to also read `efecto_inicio_pct` and `efecto_cesacion_pct`
- Modify `obtenerElasticidades()` to return `List<ElasticidadGrupo>` with `(grupoEdad, elasticidad)` pairs instead of just values

### `constantes_simulador` — New rows
```sql
INSERT INTO constantes_simulador (clave, valor, descripcion, fuente, fuente_url, anio_referencia, activo)
VALUES ('SIM_TENDENCIA_NATURAL_PP_ANUAL', -0.34, 'Declinación tendencial prevalencia pp/año', 'ENCODAT 2016-2025', 'https://encodat.org', 2025, 1);

INSERT INTO constantes_simulador (clave, valor, descripcion, fuente, fuente_url, anio_referencia, activo)
VALUES ('SIM_ANIO_BASE', 2025, 'Año base de la simulación', 'AirTellecta', '', 2025, 1);

INSERT INTO constantes_simulador (clave, valor, descripcion, fuente, fuente_url, anio_referencia, activo)
VALUES ('SIM_TAU_CONVERGENCIA', 2.5, 'Constante tau para convergencia exponencial de políticas', 'SimSmoke methodology', '', 2021, 1);
```

### API Response — Backwards Compatible

Existing fields unchanged. New optional fields added:

```java
// ProyeccionAnualDto — new fields
public BigDecimal prevalenciaBaselinePct;    // prevalence WITHOUT intervention for this year
public long defuncionesEvitadasAcumuladas;   // cumulative deaths avoided up to this year

// SimulacionResultadoDto — new field
public String metodoVersion;                 // "v2-enhanced"
```

### `PoliticaAplicadaDto` — Extended
```java
public BigDecimal efectoPct;          // existing: prevalence effect
public BigDecimal efectoInicioPct;    // new: initiation reduction effect
public BigDecimal efectoCesacionPct;  // new: cessation increase effect
```

## Frontend Changes

### `Simulador.tsx` — Chart modifications
- Add second `<Line>` for `prevalenciaBaselinePct` (dashed gray, labeled "Sin intervención")
- Change deaths avoided line to use `defuncionesEvitadasAcumuladas` (cumulative, growing curve)
- X-axis labels: show actual years (2025, 2026...) instead of just "1, 2, 3..."

### `simulacionModel.ts` — Delete entirely
The dual calculation path is eliminated. All simulation comes from the backend.

### `useSimulacion.ts` — Simplify
Remove the fallback that called `aplicarEfectoPoliticas()` from the frontend model. If the backend returns an error, show the error to the user. No client-side simulation.

### What does NOT change
- Simulator controls UI (policy checkboxes, tax slider, horizon selector)
- `SimulacionRequest` type sent to backend
- Summary cards (prevalencia final, muertes evitadas total, ahorro)
- PDF/Excel export reports
- `PdfChart.tsx` component

## Phase 2 (Future — NOT in scope)

Full age/sex Markov cohort model requiring:
- Cessation rates by age/sex (from literature)
- Relapse rates by quit-year (US rates from Levy et al.)
- Relative risk of death by smoking status/age/sex (from GBD)
- Age/sex prevalence matrix (derivable from ENCODAT)

Phase 2 builds on Phase 1's infrastructure (dynamic population, policy effects, cumulative deaths) and adds stratification on top.

## Testing Strategy

- Unit test: Given known inputs (prevalencia=15.06, politicas=[], impuesto=67.57, horizonte=10), verify baseline declines at -0.34pp/year
- Unit test: Given politicas=[SMOKE_FREE_RESTAURANT], verify year 1 has prevalence shift, years 2+ show ongoing effect
- Unit test: Verify deaths avoided are strictly increasing (cumulative)
- Unit test: Verify prevalenciaIntervencion < prevalenciaBaseline for every year when policies are selected
- Integration test: Full API call, verify response shape includes new fields
- Visual test: Frontend chart shows two diverging lines and a growing deaths curve
