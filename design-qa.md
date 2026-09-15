# Design QA — web frame alignment

> Historical visual checkpoint for the referenced screenshots. This records the layout at that review, not a current documentation audit or a guarantee for later changes. Temporary source images may no longer be available.

- Source visual truth: `/var/folders/15/bsnxdb1n0r9446hfq1kl71t80000gn/T/codex-clipboard-593e8b08-c862-4c13-af6c-74b039e8d032.png` and `/var/folders/15/bsnxdb1n0r9446hfq1kl71t80000gn/T/codex-clipboard-fe3fabec-62ee-4946-b13d-36149dd19ea5.png`
- Implementation screenshots: `design-qa-showcase.png` and `design-qa-docs.png`
- Combined comparison: `design-qa-comparison.png`
- Viewport: 1920 × 1000 CSS px, desktop, dark theme
- Source pixels: 3837 × 2006 and 3836 × 2009, approximately 2× density
- Implementation pixels: 1920 × 1000 and 1905 × 992; comparison normalized to half-width panels
- State: Showcase empty state and Docs introduction

## Findings

No actionable P0, P1 or P2 differences remain for the requested adjustments.

- Fonts and typography: Rocky, Showcase and Docs now render at 13 px in the navigation. Family, weight and hierarchy remain consistent with the reference.
- Spacing and layout: navigation, page sections and footer all measure 1120 px at the desktop breakpoint. Docs reaches at least the viewport bottom. Showcase uses the remaining height and places its footer at the bottom without moving the callout away from its original top spacing.
- Colors and tokens: existing dark background, low-opacity borders and orange accent are unchanged.
- Image quality: these screens contain no raster product imagery; the supplied captures remain the visual source.
- Copy and content: all existing labels and page content are unchanged.

## Comparison history

1. The first implementation pinned the Showcase footer but allowed its flex item to shrink and vertically centered the callout.
2. Added `width: 100%` to the footer and restored the callout's original top alignment.
3. Post-fix browser measurements show four aligned Showcase regions at 1120 px, a footer bottom of 1000 px, Docs aligned to the same 1120 px frame, working Docs/Showcase navigation and no console errors.

Focused comparison used the navigation labels, the vertical frame edges and the Showcase footer because those are the reported mismatches. Other regions were checked in the full-view comparison.

final result: passed

---

# Design QA — Agent settings, 2026-09-15

## Source and evidence

- Source visual truth: `/var/folders/15/bsnxdb1n0r9446hfq1kl71t80000gn/T/codex-clipboard-9a022375-10e8-49e5-86b6-bc7fc319e706.png` (Apple System Settings, 759 × 727 px).
- Same-screen baseline: `/tmp/rocky-agent-before-20260915/implementation-settings-macos.png` (780 × 680 px).
- Implementation: `/tmp/rocky-agent-after-20260915/implementation-settings-macos.png` (780 × 680 px).
- Additional states: `/tmp/rocky-agent-after-20260915/implementation-agent-compact.png` (462 × 820 px) and `/tmp/rocky-agent-after-20260915/implementation-agent-english.png` (780 × 680 px).
- Viewports: native Compose 780 × 680 dp and 462 × 820 dp, captured at 1× density. CSS viewport is not applicable.
- State: Agent settings, dark theme, Portuguese/default name/direct tone; compact variant; English/edited name/analytical tone.
- Full-view comparison: source and implementation were opened in the same comparison input; baseline, revised layout, compact layout, and English state were also inspected together.
- This is an adaptation of Apple's grouped settings pattern, not a pixel clone of its General page. Different content, window chrome, palette, and screenshot dimensions are intentional. Baseline and revised Agent captures have identical viewport and state for the before/after comparison.
- Evidence is a temporary visual checkpoint; screenshot paths may not persist.

## Findings

No actionable P0/P1/P2 issues remain for the requested Agent redesign.

- Fonts and typography: existing sans-serif family retained; 13 px setting labels and 12 px secondary text create a compact hierarchy. Labels, values, and descriptions remain readable in both languages without truncation in the captured states.
- Spacing and layout rhythm: identity and communication use grouped 12 dp rounded surfaces, 16 dp inner padding, aligned 190 dp controls, and inset dividers. The name field stacks below its label in the compact variant. Cards, labels, and controls do not overlap or clip in the inspected viewports.
- Colors and tokens: existing Rocky surface, border, foreground, and orange accent tokens are preserved. The unavailable feature has a neutral badge rather than an actionable-looking switch. No native vibrancy/transparency claim is made.
- Image quality and assets: no raster imagery is required for this settings content. Existing Material vector icons are used for dropdown chevrons and selection checkmarks; no generated illustrations, emoji, or substitute artwork were added.
- Copy and content: Identity and Communication labels explain the grouping. Tone descriptions reflect the selected preference in Portuguese and English. Existing AI configuration guidance and the upcoming automatic-interruption limitation remain visible.
- Interactions: name editing, tone dropdown selection, language dropdown selection, and persistence callbacks pass Compose interaction tests at compact and wide sizes. Existing done/close behavior is unchanged. The name field has an accessible content description; standard button/menu controls and focused input border are used.

## Comparison history

1. Baseline had ungrouped labels, full-width input, and multiple independent choice buttons.
2. Grouped name and language into Identity, grouped tone and contextual help into Communication, replaced choice chips with compact dropdowns, and separated the upcoming capability into its own neutral card.
3. Revised wide Portuguese, compact Portuguese, and wide English captures were inspected. No additional P0/P1/P2 visual correction was required after the first implementation comparison.

Focused regions: name input alignment, localized dropdown values, tone help, and the upcoming-feature badge were readable in the full-resolution comparison; no extra crop was needed.

## Implementation checklist

- [x] Group settings with aligned controls and inset separators.
- [x] Preserve editable name, language selection, tone selection, and change callbacks.
- [x] Inspect compact/wide layouts and Portuguese/English states.
- [x] Keep unavailable functionality visibly non-interactive.

## Follow-up polish and test gaps

- Native desktop click-through, keyboard-only menu navigation, large text scaling, and VoiceOver were not exercised by this screenshot/Compose test pass. This is not a full accessibility audit.
- Sidebar, section header, other settings tabs, and native window chrome were intentionally outside the redesign scope.

final result: passed
