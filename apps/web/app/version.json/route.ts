import { SITE_BUILD } from "@/lib/site-updates";

export const dynamic = "force-static";

export function GET() {
  return Response.json(SITE_BUILD);
}
