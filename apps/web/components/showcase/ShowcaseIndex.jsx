import ShowcaseHeading from "./ShowcaseHeading";
import ContributeCallout from "./ContributeCallout";

export default function ShowcaseIndex({ isIndex }) {
  if (!isIndex) return null;
  return <div>
    <ShowcaseHeading />
    {/* Filtros e grade de casos serão reativados junto com os use cases. */}
    <ContributeCallout />
  </div>;
}
