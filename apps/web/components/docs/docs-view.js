import { getDocsContent, slug } from "@/content/docs";

export function docsView(selectedPage, onNavigate, locale) {
    const { pages, nav, flat, firstPage } = getDocsContent(locale);
    const page = pages[selectedPage] ? selectedPage : firstPage;
    const data = pages[page];
    const i = flat.indexOf(page);
    const prev = flat[i - 1];
    const next = flat[i + 1];

    const blocks = data.blocks.map((b) => ({
      isH2: b.type === "h2",
      isPara: b.type === "para",
      isNote: b.type === "note",
      isCode: b.type === "code",
      isList: b.type === "list",
      isSteps: b.type === "steps",
      isTable: b.type === "table",
      isCards: b.type === "cards",
      isKeys: b.type === "keys",
      text: b.text,
      file: b.file,
      items: b.items,
      cols: b.cols,
      head: b.head,
      rows: b.rows,
      slug: b.type === "h2" ? slug(b.text) : undefined,
    }));

    return {
      blocks,
      locale,
      pageTitle: data.title,
      pageGroup: data.group,
      pageLead: data.lead,
      nav: nav.map((g) => ({
        title: g.title,
        items: g.items.map((id) => ({
          name: pages[id].title,
          go: () => onNavigate(id),
          color: id === page ? "#f2efec" : "rgba(255,255,255,.48)",
          bg: id === page ? "rgba(255,255,255,.07)" : "transparent",
        })),
      })),
      toc: data.blocks.filter((b) => b.type === "h2").map((b) => ({ name: b.text, href: "#" + slug(b.text) })),
      hasPrev: !!prev,
      hasNext: !!next,
      prevTitle: prev ? pages[prev].title : "",
      nextTitle: next ? pages[next].title : "",
      goPrev: () => prev && onNavigate(prev),
      goNext: () => next && onNavigate(next),
    };
}
