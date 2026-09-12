import HeroContent from "./HeroContent";

export default function HeroSection({ chord, heroChips, heroConvo, heroTabs, mic }) {
  return (<>
<div style={{"position": "relative", "borderBottom": "1px solid rgba(255,255,255,.07)"}}>
<div style={{"position": "absolute", "inset": "0", "backgroundImage": "linear-gradient(rgba(255,255,255,.028) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,.028) 1px, transparent 1px)", "backgroundSize": "56px 56px", "maskImage": "radial-gradient(80% 60% at 50% 20%, #000 0%, transparent 75%)", "pointerEvents": "none"}}>

</div>
<div style={{"position": "absolute", "inset": "0", "background": "radial-gradient(50% 40% at 50% 0%, rgba(212,112,60,.14), transparent 70%)", "pointerEvents": "none"}}>

</div>
<HeroContent chord={chord} heroChips={heroChips} heroConvo={heroConvo} heroTabs={heroTabs} mic={mic} />
</div>
  </>);
}
