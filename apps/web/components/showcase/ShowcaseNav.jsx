import SiteNav from "@/components/SiteNav";
export default function ShowcaseNav({ backToIndex }) {
  return <SiteNav active="showcase" onShowcase={backToIndex} />;
}
