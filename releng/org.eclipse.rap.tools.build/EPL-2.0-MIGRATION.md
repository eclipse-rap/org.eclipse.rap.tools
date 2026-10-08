# EPL 2.0 migration

RAP Tools now declares EPL-2.0. Copyright years, ownership statements,
contributors, ongoing-development credits, and bugfix credits are retained.

## Official guidance and migration steps

1. Adopt the successor license using section 7 of the
   [EPL 1.0 agreement](https://www.eclipse.org/legal/epl-v10.html).
   This permits a contributor to distribute the program and its contributions
   under a newly published license version. The Eclipse Foundation's
   [migration explanation](https://www.eclipse.org/lists/locationtech-pmc/msg00748.html)
   describes updating headers and notices and communicating the change publicly.
   Sections 3.1 and 3.4 of the [official EPL 2.0 FAQ](https://www.eclipse.org/legal/epl-2.0/faq/)
   describe migration without a secondary license: update notices and headers
   and replace Software User Agreements with the complete EPL 2.0 agreement.
   The FAQ loads its text through JavaScript from
   [this official content file](https://www.eclipse.org/legal/documents/html/epl-2.0-faq.html).
2. Replace the agreement itself with the complete official
   [plain text](https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt) and
   [HTML](https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html) documents.
   Changing the title of the old agreement is insufficient.
3. Update license notices and SPDX identifiers, keeping copyright and
   attribution. The [Eclipse Project Handbook](https://www.eclipse.org/projects/handbook/#legaldoc)
   describes source notices, repository license and notice files, Maven metadata,
   plug-in about files, and feature legal documentation. Apply the same changes
   to editor templates, schema documentation, and displayed feature copyrights.
4. Ensure distributions contain the new agreement. Update feature includes,
   plug-in binary/source includes, and update-site legal files. Features supply
   the complete EPL 2.0 agreement in `license.html` and the `license` element
   in `feature.xml` shown during installation. No shared Software User Agreement is used.
5. Audit declarations, attribution, and built archives using the commands below.
   Announce the license change to downstream consumers and update external
   Eclipse project/release metadata before publishing the migrated release.

This is an EPL-2.0 migration without opting into a GPL secondary license.
[Section 3.2 and Exhibit A of EPL 2.0](https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html)
require a separate secondary-license notice; merely copying the agreement,
including its Exhibit A, does not enable secondary licensing. The archived
Foundation explanation distinguishes ordinary version migration from adding
secondary-license permissions.

## Changes in this repository

* `LICENSE` contains the official EPL 2.0 plain text. Only trailing spaces
  from the official download have been removed; the license wording is unchanged.
* Project headers, Eclipse JDT templates, API documentation generation,
  README, NOTICE, and displayed feature notices now declare EPL-2.0.
  Header notices use the FAQ wording `which is available at`. Existing
  copyright years, owners, contributor credits, and `All rights reserved`
  statements are retained.
* Maven root and parent metadata declare EPL 2.0.
* All seven plug-in/test projects with manifests and build properties include
  `about.html` and `about_files/epl-2.0.html` in binary and source packaging.
  Manifests declare the EPL 2.0 license URL. Third-party declarations are preserved.
* Both features include the official EPL 2.0 HTML as both `epl-2.0.html`
  and `license.html`. Each `feature.xml` supplies the complete plain-text
  EPL 2.0 directly in its `license` element. The shared SUA feature
  dependency and its CBI repository have been removed.
* The update-site legal directory contains the official EPL 2.0 HTML in both
  `epl-2.0.html` and `notice.html`; the old SUA is replaced in place.

Third-party declarations in `NOTICE.md` are preserved. Bundled dependencies
retain their upstream licensing; this migration changes RAP Tools project
notices and does not relicense third-party code.

## Verification

Run from the repository root:

```sh
python releng/org.eclipse.rap.tools.build/scripts/verify-epl.py --baseline-ref f0769c7aa147cc553fe53016b2499ac99d4f7648
mvn -B -DskipTests -Djgit.dirtyWorkingTree=warning clean package
python releng/org.eclipse.rap.tools.build/scripts/verify-epl.py --artifacts --baseline-ref f0769c7aa147cc553fe53016b2499ac99d4f7648
```

In PowerShell, quote the Maven `-D` arguments. The package build skips runtime
tests but compiles their sources. The audit compares original copyright notices,
contributor sections, ongoing development and bugfix credits, and Java source
bodies against the pre-migration commit. It verifies exact official agreement
hashes, header wording, SPDX tags, Maven/OSGi metadata, binary/source legal file
inclusion, feature installation licenses, built archives, and actual p2 license
metadata in the regular and self-contained update sites.

Both update sites copy the official agreement as `epl-2.0.html` and `notice.html`.
The self-contained site previously omitted the legal directory; its build now
copies it with the same packaging step as the regular site.

`workbench_launch.template` remains unchanged from the pre-migration checkout
and is excluded from the migration changes.

External PMI metadata, project website, release announcement, and public
migration coordination must be handled before publishing a migrated release.
Previously released artifacts retain their original notices. This repository
migration does not modify those external resources.

## Validation results

The Maven clean package build passed for all 12 reactor modules. Runtime tests
were skipped; their sources compiled successfully. The final audit passed for
380 text files, seven plug-in/test projects, two features, 16 binary/source/feature
JARs, and both update sites and downloadable ZIPs. Actual p2 installation metadata
contains the complete EPL 2.0 agreement with a resolved `license.html` URL.

Baseline comparisons preserve all 245 original copyright notices, contributor
sections, and ongoing development and bugfix credits. Java source bodies are
unchanged. Modified XML parses, and the project-notice whitespace check passes.
Official agreement HTML is preserved byte-for-byte, including its whitespace.
