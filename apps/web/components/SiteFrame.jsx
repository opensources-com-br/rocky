export default function SiteFrame({ as: Tag = "div", className = "", ...props }) {
  return <Tag className={`site-frame ${className}`} {...props} />;
}
