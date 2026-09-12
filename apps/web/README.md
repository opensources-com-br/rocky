# Rocky landing page

This directory is the Rocky landing page, built with Next.js. It currently keeps the default starter screen; the previous design is preserved in `../landing` as a reference.

From this directory, run `npm ci` and `npm run dev` to develop locally.
Run `npm run lint` and `npm run build` to validate. The static build is written to `out/`.

The `Site` workflow publishes `out/` to GitHub Pages on pushes to `main`. It obtains `NEXT_PUBLIC_BASE_PATH` from the Pages configuration so assets also work under the repository subpath. Locally, the base path defaults to `/`.

To reproduce a project-site build: `NEXT_PUBLIC_BASE_PATH=/rocky npm run build`.
