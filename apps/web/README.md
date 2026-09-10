# Website

Rocky's static landing page. English is served from `/` and Brazilian Portuguese from `/pt-BR/`.

Preview it locally with:

```shell
python3 -m http.server 4173 --directory apps/web
```

The `Site` GitHub Actions workflow publishes this directory to GitHub Pages after changes reach `main`.
