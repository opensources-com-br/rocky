# Rocky landing page

This directory is the Rocky landing page, built with Next.js. It reproduces the supplied Landing, Docs and Showcase HTML references using native React sections and shadcn/ui controls.

From this directory, run `npm ci` and `npm run dev` to develop locally.
Run `npm run lint`, `npm test` and `npm run build` to validate. The static build is written to `out/`.

The `Site` workflow publishes `out/` to GitHub Pages on pushes to `main`. It obtains `NEXT_PUBLIC_BASE_PATH` from the Pages configuration so assets also work under the repository subpath. Locally, the base path defaults to `/`.

To reproduce a project-site build: `NEXT_PUBLIC_BASE_PATH=/rocky npm run build`.

## Updates

Every build exports `/version.json` with the same version and build timestamp embedded in the browser bundle. `NEXT_PUBLIC_SITE_VERSION` defaults to `GITHUB_SHA` in CI and a local timestamp otherwise; `NEXT_PUBLIC_SITE_BUILT_AT` accepts an epoch timestamp in milliseconds and defaults to build time. Set these before building if supplying custom deployment metadata, and deploy the entire `out/` directory together.

Open tabs check that file on startup, focus, reconnect, and every five minutes while visible. Requests bypass caches and time out after ten seconds. A newer build shows a localized **Reload site** / **Later** notice; the site never reloads automatically. Reload preserves the route, query, fragment, and local preferences, and adds a version query parameter to avoid stale HTML. Static hosting needs no server, service worker, or special response headers.

Desktop downloads use published official releases containing `SHA256SUMS.txt` (or the legacy `SHA256SUMS`). The browser downloads only a universal or matching architecture installer; unidentified or unavailable architectures and GitHub failures fall back to the official releases page. The desktop updater and the site's reload notice are independent. Older desktop builds require one installation over the existing app to gain the built-in updater.

## Pages and components

- `/`: landing, animated app preview, features and download links.
- `/docs/`: 17 topics with sidebar, table of contents and previous/next navigation.
- `/showcase/`: coming-soon notice and a disabled contribution button. Cases, filters and the contribution form are currently hidden.

Content lives in `content/`; reusable controls in `components/ui/`; page sections in `components/landing/`, `components/docs/` and `components/showcase/`. shadcn/ui is configured in `components.json`.

Public copy is checked against the current desktop implementation. The nine example scenario files and contribution helpers remain in the source for future use; they are not published user stories or active features. Download and repository actions use the official Rocky repository. No contribution can currently be submitted from the web.

For browser validation, check the three routes, all 17 documentation topics, desktop/mobile layouts and the disabled showcase submission action. Original reference HTML files are not required by the React implementation.
