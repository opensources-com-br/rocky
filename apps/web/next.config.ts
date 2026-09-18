import type { NextConfig } from "next";

const builtAt = process.env.NEXT_PUBLIC_SITE_BUILT_AT ||= String(Date.now());
const siteVersion = process.env.NEXT_PUBLIC_SITE_VERSION ||= process.env.GITHUB_SHA || `local-${builtAt}`;

const nextConfig: NextConfig = {
  output: "export",
  trailingSlash: true,
  basePath: process.env.NEXT_PUBLIC_BASE_PATH || "",
  images: { unoptimized: true },
  env: {
    NEXT_PUBLIC_SITE_VERSION: siteVersion,
    NEXT_PUBLIC_SITE_BUILT_AT: builtAt,
  },
};

export default nextConfig;
