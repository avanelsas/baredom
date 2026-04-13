(function () {
  'use strict';

  var PRESETS = ['default', 'ocean', 'forest', 'sunset', 'neo-brutalist', 'aurora', 'mono-ai', 'warm-mineral'];
  var STORAGE_KEY = 'baredom-theme';
  var PARAM_KEY = 'theme';

  function getInitialPreset() {
    var params = new URLSearchParams(window.location.search);
    var fromUrl = params.get(PARAM_KEY);
    if (fromUrl && PRESETS.indexOf(fromUrl) !== -1) return fromUrl;
    try { var stored = localStorage.getItem(STORAGE_KEY); if (stored && PRESETS.indexOf(stored) !== -1) return stored; } catch (e) {}
    return null;
  }

  function savePreset(preset) {
    var params = new URLSearchParams(window.location.search);
    if (preset) { params.set(PARAM_KEY, preset); } else { params.delete(PARAM_KEY); }
    var qs = params.toString();
    var url = window.location.pathname + (qs ? '?' + qs : '') + window.location.hash;
    history.replaceState(null, '', url);
    try { if (preset) { localStorage.setItem(STORAGE_KEY, preset); } else { localStorage.removeItem(STORAGE_KEY); } } catch (e) {}
  }

  function applyPreset(preset, themeEl) {
    if (preset) {
      if (!themeEl) {
        themeEl = document.createElement('x-theme');
        var children = [];
        for (var i = 0; i < document.body.childNodes.length; i++) {
          var node = document.body.childNodes[i];
          if (!node._themePickerBar) children.push(node);
        }
        for (var j = 0; j < children.length; j++) themeEl.appendChild(children[j]);
        document.body.appendChild(themeEl);
      }
      themeEl.setAttribute('preset', preset);
    } else if (themeEl) {
      while (themeEl.firstChild) document.body.insertBefore(themeEl.firstChild, themeEl);
      themeEl.remove();
      themeEl = null;
    }
    return themeEl;
  }

  function createPicker(activePreset, onChange) {
    var bar = document.createElement('div');
    bar._themePickerBar = true;
    bar.setAttribute('style',
      'position:sticky;top:0;z-index:100;padding:6px 12px;' +
      'background:rgba(255,255,255,0.92);backdrop-filter:blur(8px);' +
      'border-bottom:1px solid rgba(0,0,0,0.1);display:flex;align-items:center;gap:6px;' +
      'font-family:system-ui,-apple-system,sans-serif;font-size:12px;font-weight:500;'
    );

    var label = document.createElement('span');
    label.textContent = 'Theme:';
    label.setAttribute('style', 'color:#64748b;margin-right:2px;flex-shrink:0;');
    bar.appendChild(label);

    // Desktop: buttons
    var btnWrap = document.createElement('div');
    btnWrap.setAttribute('style', 'display:flex;gap:4px;flex-wrap:wrap;align-items:center;');
    var allOptions = [null].concat(PRESETS);
    var buttons = [];

    for (var i = 0; i < allOptions.length; i++) {
      (function (preset) {
        var btn = document.createElement('button');
        btn.textContent = preset || 'None';
        btn.setAttribute('style',
          'all:unset;cursor:pointer;padding:3px 8px;border-radius:4px;font-size:11px;font-weight:500;' +
          'border:1px solid transparent;transition:background 80ms ease;white-space:nowrap;'
        );
        btn.addEventListener('click', function () { onChange(preset); updateActive(preset); });
        buttons.push({ el: btn, preset: preset });
        btnWrap.appendChild(btn);
      })(allOptions[i]);
    }

    // Mobile: select
    var sel = document.createElement('select');
    sel.setAttribute('style', 'font-size:12px;padding:3px 6px;border-radius:4px;border:1px solid #d1d5db;background:#fff;color:#0f172a;flex:1;min-width:0;');
    var noneOpt = document.createElement('option');
    noneOpt.value = '';
    noneOpt.textContent = 'None';
    sel.appendChild(noneOpt);
    for (var k = 0; k < PRESETS.length; k++) {
      var opt = document.createElement('option');
      opt.value = PRESETS[k];
      opt.textContent = PRESETS[k];
      sel.appendChild(opt);
    }
    sel.value = activePreset || '';
    sel.addEventListener('change', function () {
      var v = sel.value || null;
      onChange(v);
      updateActive(v);
    });

    // Responsive: show buttons on desktop, select on mobile
    var mq = window.matchMedia('(max-width:639px)');
    function applyLayout() {
      if (mq.matches) {
        btnWrap.style.display = 'none';
        sel.style.display = '';
      } else {
        btnWrap.style.display = 'flex';
        sel.style.display = 'none';
      }
    }
    mq.addEventListener('change', applyLayout);
    applyLayout();

    bar.appendChild(btnWrap);
    bar.appendChild(sel);

    // Dark mode for the bar itself
    var mqDark = window.matchMedia('(prefers-color-scheme:dark)');
    function applyBarTheme() {
      if (mqDark.matches) {
        bar.style.background = 'rgba(15,23,42,0.92)';
        bar.style.borderBottomColor = 'rgba(255,255,255,0.1)';
        label.style.color = '#94a3b8';
        sel.style.background = '#1e293b';
        sel.style.color = '#e5e7eb';
        sel.style.borderColor = '#475569';
      } else {
        bar.style.background = 'rgba(255,255,255,0.92)';
        bar.style.borderBottomColor = 'rgba(0,0,0,0.1)';
        label.style.color = '#64748b';
        sel.style.background = '#fff';
        sel.style.color = '#0f172a';
        sel.style.borderColor = '#d1d5db';
      }
      updateActive(activePreset);
    }
    mqDark.addEventListener('change', applyBarTheme);

    function updateActive(preset) {
      activePreset = preset;
      sel.value = preset || '';
      var isDark = mqDark.matches;
      for (var b = 0; b < buttons.length; b++) {
        var isActive = buttons[b].preset === preset;
        var s = buttons[b].el.style;
        if (isActive) {
          s.background = isDark ? '#334155' : '#e2e8f0';
          s.borderColor = isDark ? '#475569' : '#cbd5e1';
          s.color = isDark ? '#f1f5f9' : '#0f172a';
        } else {
          s.background = 'transparent';
          s.borderColor = 'transparent';
          s.color = isDark ? '#94a3b8' : '#64748b';
        }
      }
    }

    applyBarTheme();
    return bar;
  }

  // x-select compatibility shim for dogfooded demos.
  // x-select fires "select-change" with detail.value but does not auto-set
  // the value attribute, so el.value would otherwise stay stale. Echo it
  // back and re-dispatch a synthetic native "change" event so existing
  // demo JS that listens for "change" and reads el.value keeps working
  // without any per-file edits.
  document.addEventListener('select-change', function (e) {
    var sel = e.target;
    if (sel && sel.tagName === 'X-SELECT') {
      sel.setAttribute('value', e.detail.value);
      sel.dispatchEvent(new Event('change', { bubbles: true }));
    }
  });

  // x-switch compatibility shim. x-switch fires "x-switch-change" with
  // detail.checked after the state has updated. Re-dispatch a synthetic
  // native "change" event so existing demo JS that listens for "change"
  // and reads el.checked keeps working without per-file edits.
  document.addEventListener('x-switch-change', function (e) {
    var sw = e.target;
    if (sw && sw.tagName === 'X-SWITCH') {
      sw.dispatchEvent(new Event('change', { bubbles: true }));
    }
  });

  // x-checkbox compatibility shim — same idea as x-switch.
  document.addEventListener('x-checkbox-change', function (e) {
    var cb = e.target;
    if (cb && cb.tagName === 'X-CHECKBOX') {
      cb.dispatchEvent(new Event('change', { bubbles: true }));
    }
  });

  // x-form-field compatibility shim. x-form-field fires x-form-field-input
  // and x-form-field-change with {name, value}. Re-dispatch as synthetic
  // native "input" / "change" events so existing demo JS that listens for
  // the native events keeps working without per-file edits. el.value reads
  // already work transparently (x-form-field exposes a live value getter).
  document.addEventListener('x-form-field-input', function (e) {
    var ff = e.target;
    if (ff && ff.tagName === 'X-FORM-FIELD') {
      ff.dispatchEvent(new Event('input', { bubbles: true }));
    }
  });
  document.addEventListener('x-form-field-change', function (e) {
    var ff = e.target;
    if (ff && ff.tagName === 'X-FORM-FIELD') {
      ff.dispatchEvent(new Event('change', { bubbles: true }));
    }
  });

  document.addEventListener('DOMContentLoaded', function () {
    var preset = getInitialPreset();
    var themeEl = null;

    var picker = createPicker(preset, function (newPreset) {
      savePreset(newPreset);
      themeEl = applyPreset(newPreset, themeEl);
    });

    document.body.insertBefore(picker, document.body.firstChild);
    themeEl = applyPreset(preset, null);
  });
})();
