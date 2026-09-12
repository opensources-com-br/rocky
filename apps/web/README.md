# Rocky landing page

This directory is the Rocky landing page, built with Next.js. It reproduces the supplied Landing, Docs and Showcase HTML references using native React sections and shadcn/ui controls.

From this directory, run `npm ci` and `npm run dev` to develop locally.
Run `npm run lint`, `npm test` and `npm run build` to validate. The static build is written to `out/`.

The `Site` workflow publishes `out/` to GitHub Pages on pushes to `main`. It obtains `NEXT_PUBLIC_BASE_PATH` from the Pages configuration so assets also work under the repository subpath. Locally, the base path defaults to `/`.

To reproduce a project-site build: `NEXT_PUBLIC_BASE_PATH=/rocky npm run build`.

## Pages and components

- `/`: landing, animated app preview, features and download links.
- `/docs/`: 17 topics with sidebar, table of contents and previous/next navigation.
- `/showcase/`: 9 supplied cases, category filters, case details and a local Markdown contribution form with clipboard feedback.

Content lives in `content/`; reusable controls in `components/ui/`; page sections in `components/landing/`, `components/docs/` and `components/showcase/`. shadcn/ui is configured in `components.json`.

The supplied copy and example cases are reproduced as design content, not evidence of supported desktop integrations or verified testimonials. Download and repository actions use the official Rocky repository. No contribution is submitted automatically.

Browser validation: desktop and mobile layouts, all 17 topics, all 9 details, filters, form fields and copying. Original reference HTML files are not required by the React implementation.
