import LandingNav from "./LandingNav";
import HeroSection from "./HeroSection";
import PlatformsStrip from "./PlatformsStrip";
import HowItWorks from "./HowItWorks";
import FeaturesSection from "./FeaturesSection";
import PrivacySection from "./PrivacySection";
import DownloadSection from "./DownloadSection";
import LandingFooter from "./LandingFooter";

export default function LandingView({ features, guarantees, localCards, platforms, steps }) {
  return (<>
<div style={{"background": "#080706", "color": "#f2efec", "fontFamily": "var(--font-geist-sans), sans-serif", "overflowX": "hidden", "letterSpacing": "-.011em"}}>
<LandingNav  />
<HeroSection />
<PlatformsStrip platforms={platforms} />
<HowItWorks steps={steps} />
<FeaturesSection features={features} />
<PrivacySection guarantees={guarantees} localCards={localCards} />
<DownloadSection  />
<LandingFooter  />
</div>
  </>);
}
