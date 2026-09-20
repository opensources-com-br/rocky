import ShowcaseHeading from "./ShowcaseHeading";
import ContributeCallout from "./ContributeCallout";

export default function ShowcaseIndex({ isIndex, locale }) {
  if (!isIndex) return null;
  return <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
    <ShowcaseHeading locale={locale} />
    {/* Filters and the case grid will return with the use cases. */}
    <ContributeCallout locale={locale} />
  </div>;
}
