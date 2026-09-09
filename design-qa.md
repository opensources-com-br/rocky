# Design QA

## Source truth

- The nine Rocky mockups attached to the implementation task are the visual source of truth.
- They cover the main conversation, Superchats, notes, ideas, pulse, and the four settings panels.
- The implementation was rendered at `420 × 720` for the main window and `420 × 520` for settings.
- Local comparison artifact: `/tmp/rocky-design-qa/comparison-final.png`.

## Pass 1

- **P2 · Typography:** the assistant prompt was too large and wrapped earlier than the reference. Reduced the display style from 31/38 to 28/34.
- **P2 · Content density:** the main window did not leave enough room for the conversation. Increased its height to 720 and matched the prompt wrapping.
- **P2 · Copy:** the support tab used “Apoios” while the mockups use “Superchats”. Updated the label.
- **P2 · Settings frame:** settings left excess empty space and did not match the shorter reference frame. Added a 520-height settings mode that restores the previous window size on exit.
- **P2 · Settings rhythm:** fields, sliders, toggles, and vertical gaps exceeded the source density. Tightened their sizes and spacing.

## Pass 2

- **Layout and spacing:** header, platform strip, assistant prompt, actions, tabs, scrolling content, and footer preserve the source order and grouping. No overlap was found at either target size.
- **Typography:** Instrument Serif is bundled for the main prompt; system sans is used for compact desktop UI text. Weight, wrapping, and hierarchy match the source intent.
- **Color and surfaces:** the dark neutral palette, brown prompt gradient, orange accent, platform colors, borders, selected states, and disabled states are represented by shared tokens.
- **Icons and assets:** visible symbols use the Compose Material icon library. No placeholder imagery, custom vector art, or text glyph substitutes are present.
- **States and interactions:** all five main tabs and all four settings tabs render; prompt actions, voice state, support read state, settings selections, text input, sliders, toggles, pin, minimize, close, and compact mode are wired.
- **Accessibility:** interactive icons have descriptions, colored states retain text labels, controls remain keyboard reachable, and scrollable regions prevent content loss.
- **Verification:** the desktop UI test rendered and asserted all nine target states. The complete Gradle build also passed.

No P0, P1, or P2 findings remain.

final result: passed
