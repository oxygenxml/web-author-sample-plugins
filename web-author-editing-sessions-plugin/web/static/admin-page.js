/**
 * Script of the "Editing sessions viewer" page shown in the Administration Page
 * (Plugins -> Editing sessions viewer -> Configure...).
 *
 * It is loaded as an external file (not inline) because the Administration Page has a
 * Content-Security-Policy with script-src 'self', which blocks inline scripts.
 *
 * The page is written by the Administration Page in an iframe (about:blank), so relative URLs
 * are resolved against the Administration Page URL ({context}/app/admin.html).
 */
(function () {
  var PAGE_URL = '../plugins-dispatcher/editing-sessions';

  /** Re-requests the page from the server and replaces the content. */
  function refresh() {
    fetch(PAGE_URL, {
      headers: {'Accept': 'text/html', 'X-Requested-With': 'WA'},
      credentials: 'same-origin'
    }).then(function (r) {
      if (!r.ok) {
        throw new Error('HTTP ' + r.status);
      }
      return r.text();
    }).then(function (text) {
      var doc = new DOMParser().parseFromString(text, 'text/html');
      document.getElementById('content').innerHTML = doc.getElementById('content').innerHTML;
      bindRefresh();
    }).catch(function (e) {
      alert('Could not refresh: ' + e.message);
    });
  }

  function bindRefresh() {
    var button = document.getElementById('refresh');
    if (button) {
      button.onclick = refresh;
    }
  }

  /**
   * The Administration Page always adds "Restore Defaults", "Apply" and "Cancel" buttons to a
   * plugin configuration dialog. This page has no options, so hide the first two and rename
   * "Cancel" to "Close".
   */
  function adjustDialogButtons() {
    var frame = window.frameElement;
    var dialog = frame && frame.closest('.modal-dialog');
    if (!dialog) {
      return;
    }
    ['apply', 'restore_defaults'].forEach(function (name) {
      var button = dialog.querySelector('.modal-dialog-buttons button[name="' + name + '"]');
      if (button) {
        button.style.display = 'none';
      }
    });
    var cancel = dialog.querySelector('.modal-dialog-buttons button[name="cancel"]');
    if (cancel) {
      cancel.textContent = 'Close';
    }
  }

  bindRefresh();
  // Run after the Administration Page finished setting up the dialog.
  setTimeout(function () {
    adjustDialogButtons();
    // Fallback, in case the buttons are still triggered (e.g. Enter key on the default button):
    // just close the dialog instead of trying to save options.
    window.onmessage = function (e) {
      if (e.data && (e.data.action === 'apply' || e.data.action === 'restoreDefaults')) {
        e.source.postMessage({done: true, message: 'Closed'}, e.origin);
      }
    };
  }, 0);
})();
