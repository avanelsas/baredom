# Prerendering a page

A page of web components shows nothing until its scripts have run. A prerendered page has the
shadow tree of every component in its markup, as a declarative shadow root, so the browser draws
the page as soon as the markup arrives. When the scripts arrive, each component takes over its
own tree.

`scripts/prerender.mjs` makes such a page. It opens a page in a headless Chrome and writes it
back out with the browser's own `getHTML`. It works for a page with open shadow roots, whatever
it was built with.

## Use

Serve the page, then:

    node scripts/prerender.mjs http://localhost:8000/page.html page.prerendered.html

It needs Node 22 or later, and Chrome. Set `CHROME_PATH` when Chrome is not found, and
`CHROME_FLAGS` for more flags, such as `--no-sandbox` in a container.

Write the output beside the page it was made from, so the relative URLs in it still resolve.

The script waits until every custom element on the page is defined, and then for two frames.

## What you get

- The page is drawn before any script runs, in the theme's colours.
- On the pages we watched, nothing flashed or moved when the scripts took over. A component
  builds its tree again in the shadow root that is already there.
- A keyed row that is in the markup is adopted by BareMirror's `sync`. It is not made again.

## Limits

- **Your script runs again, on a page that already shows its result.** The prerendered markup
  holds what your script made the first time. A script that adds to the page at start, such as
  `list.append(row)`, shows every row twice. A script must bring the page to its state and add
  nothing. BareMirror's `sync` does that: it finds the keyed rows that are there and makes only
  the ones that are missing.
- **For pages whose markup is known ahead of the request.** The script runs when you build a
  page, not when a visitor asks for one. It is not a server renderer.
- **The page looks ready before it is.** Until the scripts have arrived, a click does nothing.
- **The page grows.** Each shadow root carries the styles of its component. A page of 8 KB
  became 79 KB, and 7.5 KB gzipped, since the copies compress well.
- **What depends on the moment is frozen.** A calendar shows the month of the day the page was
  prerendered, until its script runs.
- **Every custom element on the page must be defined.** Import the module of each component the
  page uses, children such as `x-table-row` and `x-tab` included. The script stops and names an
  element that is never defined.
- **A closed shadow root is not written out.** The script cannot see into it.
- **Build the page from the release files.** A development build writes its own reload notice
  into the page.
- **Tried in Chrome.** The takeover was watched in Chrome. Firefox and Safari read declarative
  shadow roots too, and were not watched.
