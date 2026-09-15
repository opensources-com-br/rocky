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

---

# Design QA — AI settings, 2026-09-15

## Source and evidence

- Source visual truth: `/tmp/rocky-agent-after-20260915/implementation-settings-macos.png` (780 × 680 px) and `/tmp/rocky-agent-after-20260915/implementation-agent-compact.png` (462 × 820 px). These are the Agent layouts explicitly selected as the pattern for AI.
- Implementation: `/tmp/rocky-ai-qa-20260915/implementation-ai-local.png` (780 × 680 px) and `/tmp/rocky-ai-qa-20260915/implementation-settings-ai.png` (462 × 820 px).
- Focused states in the same evidence directory: `implementation-ai-credentials.png`, `implementation-ai-advanced.png`, `implementation-ai-context.png`, `implementation-ai-automatic.png`, and `implementation-ai-verified.png` (780 × 680 px each).
- Viewports: native Compose 780 × 680 dp and 462 × 820 dp; screenshots at 1× density, no downsampling. CSS viewport is not applicable.
- State: Portuguese, dark theme; Ollama/default model; OpenRouter/empty key; advanced endpoint expanded; proactive profile/context filters expanded; model listing and connection test exercised with a fake client.
- Source and AI captures were opened in the same comparison input at their original dimensions, including compact variants. Expanded connection, credential, and context regions were inspected at full resolution; their text and controls were readable without additional crops.
- This is an adaptation of Agent's layout to a longer, scrollable settings form, not an exact content clone. Different section titles, provider-specific fields, and offscreen groups during scrolling are intentional. Sidebar and section header remain persistent.
- Paths refer to temporary checkpoint screenshots and may not persist.

## Findings and required fidelity surfaces

No actionable P0/P1/P2 visual issues remain for this layout adaptation.

- Fonts and typography: existing Rocky sans-serif hierarchy is retained. Group labels, setting labels, values, field labels, and secondary help have distinct weights and sizes. Provider labels and compact controls fit the inspected widths.
- Spacing and layout: Agent's group, row, and menu components are extracted into shared settings components without visual changes to Agent. AI uses the same 12 dp card radius, 16 dp inner padding, inset dividers, 190 dp selector alignment, and 18 dp group gaps. Editable model/address/key fields use full width to accommodate long values. Groups and controls do not overlap; lower groups are reachable by vertical scrolling.
- Colors and tokens: existing surface/background/border tokens remain consistent with Agent. Orange is reserved for selected states, actions, and focus; key deletion uses the existing red token. Credential fields retain masked/unmasked states and reset visibility on provider changes.
- Image quality and assets: the form needs no raster imagery. Existing Material vector chevrons/checkmarks and credential-visibility icons are retained; no generated assets or substitute artwork were introduced.
- Copy and content: Connection, Credentials, Verification, Behavior/context, and Automatic suggestions explain the grouping. Provider warnings, API cost notices, local processing guidance, system-vault guidance, filter scope, automatic limits, and batch behavior remain represented. New headings and controls use the existing localization mechanism.

## Interaction verification

- All provider configuration tests pass with the dropdown replacing provider chips.
- Model listing and choosing a returned model are tested using a fake client; no external API requests were made for this QA.
- Connection test reaches its success status; automatic analysis still uses the original state callback.
- Advanced endpoint and context filters expand; bot editing and proactive profile selection remain functional.
- API key is editable and can be revealed/hidden. Test-only key input was cleared before screenshots; no secrets are recorded.
- The complete UI test suite and desktop compilation pass, including existing Agent tests after shared-component extraction.

## Comparison history and checklist

1. Used Agent's existing wide and compact captures as the selected source pattern.
2. Applied shared grouped surfaces and compact menus to AI; moved provider configuration first and grouped provider-specific credentials, verification, and context controls.
3. Inspected wide/compact, local/cloud, expanded endpoint/filter, and final automatic-suggestions captures. No further P0/P1/P2 visual corrections were required after the initial comparison. Additional scrolled captures expose lower card content intentionally outside the initial viewport.

- [x] Reuse Agent's visual primitives without changing Agent's appearance.
- [x] Preserve provider/model/key configuration and connection actions.
- [x] Preserve profile, filter, and automatic-analysis controls.
- [x] Inspect wide/compact and expanded states.
- [x] Pass UI tests and desktop compilation.

Follow-up test gaps: keyboard-only navigation, VoiceOver, larger text scaling, native desktop click-through, and English AI screenshots were not part of this pass. This is not a full accessibility audit. Window chrome, other tabs, and menu-bar translucency are outside this change.

final result: passed

# Voice settings — grouped macOS-style layout (2026-09-15)

## Source and evidence

- Selected source pattern: AI captures `/tmp/rocky-ai-qa-20260915/implementation-ai-local.png` (780 × 680 px) and `implementation-settings-ai.png` (462 × 820 px), continuing the Apple-inspired Agent layout.
- Implementation checkpoints: `/tmp/rocky-voice-qa-20260915/implementation-voice-local.png` (780 × 680 px) and `implementation-settings-voice.png` (462 × 820 px).
- Expanded states in the same directory: `implementation-voice-recognition.png`, `implementation-voice-detection.png`, `implementation-voice-diagnostics.png`, `implementation-voice-cloud.png` (780 × 680 px); `implementation-voice-compact-recognition.png` and `implementation-voice-compact-diagnostics.png` (462 × 820 px).
- Native Compose viewports at 1× density, Portuguese, dark theme. CSS viewport and web-browser QA do not apply. Temporary checkpoint files may not persist.
- Source and implementation images were opened together in the same comparison inputs, including compact and expanded states. Text, fields, chevrons, sliders, and switches were readable at original resolution without extra crops.
- This adapts the established grouping and visual hierarchy to Voice rather than cloning AI's content. Vertical scrolling and partially visible neighboring cards in scrolled captures are intentional; the wide sidebar/header and compact tab bar remain fixed.

## Findings and required fidelity surfaces

No actionable P0/P1/P2 visual issues were found in the comparison pass.

- Fonts/typography: existing Rocky sans-serif styling is retained, with secondary group headings/help, medium row labels, and right-aligned slider values. Long recognition and volume descriptions wrap without colliding with controls at either width.
- Spacing/layout: shared 12 dp group surfaces, 16 dp internal padding, inset dividers, and 190 dp menus match AI. Playback, microphone input, detection, conversation testing, and diagnostics are separated. Whisper paths and audio timings are collapsed initially to reduce density. Advanced path fields and browse buttons fit the compact width.
- Colors/tokens: existing background, elevated surface, border, text, and accent tokens are reused. Fields use dark backgrounds and accent focus borders; disabled conversation/catalog actions retain disabled styling. Existing volume availability behavior is unchanged.
- Image quality/assets/icons: no raster assets are required. Existing Material chevrons and selected-item checkmarks are used; no generated artwork or imitation icons were added.
- Copy/content: cloud-processing/credit and OBS notices remain. Recognition describes the agent wake name, silence detection, maximum phrase length, and capture pause. System fallback, local setup, manual model paths, and telemetry remain accessible. Shortcut settings remain absent.

## Interaction verification

- New tests exercise system voice and microphone selection, suggestion reading, playback, manual recognition paths, detection toggle, expanded diagnostics, ElevenLabs selection, editable voice/model IDs, and local fallback.
- Compact tests open recognition and diagnostics; existing tests still cover automatic recognition setup and a complete transcribe/respond conversation.
- Fake devices/services are used; no real microphone capture, cloud requests, account credits, or secrets are involved in this QA.
- Complete `:shared:ui:desktopTest` suite and `:apps:desktop:compileKotlinJvm` pass.
- Initial visual comparison required no P0/P1/P2 code corrections. Additional scrolled checkpoints exposed lower/expanded content at both supported sizes.

Follow-up gaps: real-device microphone/audio verification, catalog loading against ElevenLabs, keyboard-only operation, VoiceOver, larger text scaling, English screenshots, and native desktop click-through were not exercised. This is not a full accessibility audit. Other tabs and menu-bar behavior are outside scope.

final result: passed
