import { getSteps } from "./steps";
import { getPlatforms } from "./platforms";
import { getFeatures } from "./features";
import { getLocalCards } from "./localCards";
import { getGuarantees } from "./guarantees";

export function getLandingContent(locale) {
  return {
    steps: getSteps(locale),
    platforms: getPlatforms(locale),
    features: getFeatures(locale),
    localCards: getLocalCards(locale),
    guarantees: getGuarantees(locale),
  };
}
