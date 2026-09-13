import LandingNav from "./LandingNav";
import HeroSection from "./HeroSection";
import PlatformsStrip from "./PlatformsStrip";
import HowItWorks from "./HowItWorks";
import FeaturesSection from "./FeaturesSection";
import PrivacySection from "./PrivacySection";
import DownloadSection from "./DownloadSection";
import LandingFooter from "./LandingFooter";

export default function LandingView({ features, guarantees, localCards, locale, platforms, steps }) {
  return (<>
<div style={{"background": "#080706", "color": "#f2efec", "fontFamily": "var(--font-geist-sans), sans-serif", "letterSpacing": "-.011em"}}>
<LandingNav  />
<HeroSection locale={locale} />
<PlatformsStrip locale={locale} platforms={platforms} />
<HowItWorks locale={locale} steps={steps} />
<FeaturesSection features={features} locale={locale} />
<PrivacySection guarantees={guarantees} localCards={localCards} locale={locale} />
<DownloadSection locale={locale} />
<LandingFooter locale={locale} />
</div>
  </>);
}
