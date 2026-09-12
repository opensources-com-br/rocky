import * as content from "@/content/landing";
import { Chord, MicrophoneWave } from "@/components/Chord";
import LandingView from "./LandingView";
export default function Landing() {
  return <LandingView {...content} chord={<Chord />} mic={<MicrophoneWave />} />;
}
