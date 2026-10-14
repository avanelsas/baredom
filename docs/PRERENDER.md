# Prerendering a page

A page of web components shows nothing until its scripts have run. A prerendered page has the
shadow tree of every component in its markup, as a declarative shadow root, so the browser draws
the page as soon as the markup arrives. When the scripts arrive, each component takes over its
own tree.

`scripts/prerender.mjs` makes such a page. It opens a page in a headless Chrome and writes it
back out with the browser's own `getHTML`. It works for a page with open shadow roots, whatever
it was built with.

The script is in the npm package, as the command `baredom-prerender`.

## Use

Serve the page, then:

    node scripts/prerender.mjs http://localhost:8000/page.html page.prerendered.html

With the package installed, the same is:

    npx baredom-prerender http://localhost:8000/page.html page.prerendered.html

Give more pairs of a URL and a file to write more pages. One browser opens them all, which is
much quicker than a run for each page:

    node scripts/prerender.mjs http://localhost:8000/ index.html http://localhost:8000/about/ about/index.html

Four pages are open at once, each in a tab of its own, or fewer on a machine with fewer
processors. `--tabs` says how many:

    node scripts/prerender.mjs --tabs 8 http://localhost:8000/ index.html ...

Each page is written when it is done, so the pages are written in the order in which they are
done, not in the order they were given. Each line that the script prints names its file.

More tabs help as far as the machine does. On a machine with ten processors, 120 pages from a
plain file server took 13 seconds with one tab, 6.9 with four and 5.6 with eight. 351 pages of
a blog took 38 seconds with one tab and 18 with four, the work of the blog's own server
included. More tabs than processors do not help.

A page that fails stops the run: no tab takes another page, the pages that are open are
written, and the script ends with the first error and says how many more failed. The pages
written before it stay written.

It needs Node 22 or later, and Chrome. Set `CHROME_PATH` when Chrome is not found, and
`CHROME_FLAGS` for more flags, such as `--no-sandbox` in a container.

Write the output beside the page it was made from, so the relative URLs in it still resolve.

The script waits until every custom element on the page is defined, and then for two frames.
A page that loads more after that puts `data-loading` on its `html` element and removes it when
it is ready. The script waits for that too.

It then waits until every image has loaded or failed, in the page and in every shadow tree. An
image that the browser loads lazily is loaded for this, and is lazy again in what is written.
So an `x-image` is written as loaded, and the same page gives the same file each time. The script
stops and names an image that never finishes.

The directory of an output file is made when it is not there.

With `--static` before the URL, the output has no `script` and no `noscript` element, and its
`html` element has `data-static`. The page can use that attribute in its style to hide what
needs a script. [`bare-html/mirror.html`](../bare-html/mirror.html) does.

## What you get

- The page is drawn before any script runs, in the theme's colours.
- On the pages we watched, nothing flashed or moved when the scripts took over. A component
  builds its tree again in the shadow root that is already there. An `x-image` whose picture
  the page has already is loaded at once, with no shimmer and no fade.
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
- **A page is drawn as if its window has the focus.** Chrome draws a tab in the background
  more slowly, so every tab is told that it has the focus, also when there is one. An element
  with `autofocus` takes it. One page with an `x-otp-input` and an `input` that both have
  `autofocus` was written the same with the focus as without it.
- **Tried in Chrome.** The takeover was watched in Chrome. Firefox and Safari read declarative
  shadow roots too, and were not watched.
