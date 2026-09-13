import ShowcaseHeading from "./ShowcaseHeading";
import ContributeCallout from "./ContributeCallout";

export default function ShowcaseIndex({ isIndex, locale }) {
  if (!isIndex) return null;
  return <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
    <ShowcaseHeading locale={locale} />
    {/* Filtros e grade de casos serão reativados junto com os use cases. */}
    <ContributeCallout locale={locale} />
  </div>;
}
