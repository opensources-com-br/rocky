import { PAGES, NAV, FLAT, slug } from "@/content/docs";
export function docsView(selectedPage, onNavigate) {
    const page = PAGES[selectedPage] ? selectedPage : "Introdução";
    const data = PAGES[page];
    const i = FLAT.indexOf(page);
    const prev = FLAT[i - 1];
    const next = FLAT[i + 1];

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
      pageTitle: page,
      pageGroup: data.group,
      pageLead: data.lead,
      nav: NAV.map((g) => ({
        title: g.title,
        items: g.items.map((name) => ({
          name,
          go: () => onNavigate(name),
          color: name === page ? "#f2efec" : "rgba(255,255,255,.48)",
          bg: name === page ? "rgba(255,255,255,.07)" : "transparent",
        })),
      })),
      toc: data.blocks.filter((b) => b.type === "h2").map((b) => ({ name: b.text, href: "#" + slug(b.text) })),
      hasPrev: !!prev,
      hasNext: !!next,
      prevTitle: prev || "",
      nextTitle: next || "",
      goPrev: () => prev && onNavigate(prev),
      goNext: () => next && onNavigate(next),
    };
}
