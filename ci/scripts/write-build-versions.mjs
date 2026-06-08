#!/usr/bin/env node
import { writeFileSync } from 'node:fs';

import { enrichBuildJson, versionsFromArgv } from '../lib/build-json.mjs';

const args = process.argv.slice(2);
if (args.length < 7) {
  console.error('usage: write-build-versions.mjs <versions...4> <bundleId> <hashLen> <outPath>');
  process.exit(1);
}

const [bundleId, hashLen, outPath] = args.slice(4);
const versions = versionsFromArgv(args);
const data = enrichBuildJson(versions, bundleId, hashLen);

writeFileSync(outPath, `${JSON.stringify(data, null, 2)}\n`);
