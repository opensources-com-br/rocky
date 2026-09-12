# Design QA — web frame alignment

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
