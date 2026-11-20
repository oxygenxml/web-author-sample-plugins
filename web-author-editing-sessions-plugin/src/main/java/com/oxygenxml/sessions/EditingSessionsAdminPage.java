package com.oxygenxml.sessions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ro.sync.ecss.extensions.api.webapp.plugin.ServletPluginConfigExtension;

/**
 * Contributes a page in the Web Author Administration Page
 * (Administration Page -&gt; Plugins -&gt; "Editing sessions viewer" -&gt; Configure)
 * that lists the users that currently have documents open.
 * <p>
 * The page is (re)generated on the server every time it is requested, so it always shows
 * the current state. It also has a "Refresh" button.
 * <p>
 * Being a {@link ServletPluginConfigExtension}, it is available only to authenticated
 * administrators.
 */
public class EditingSessionsAdminPage extends ServletPluginConfigExtension {

  /** The servlet path: {context}/plugins-dispatcher/editing-sessions */
  private static final String PATH = "editing-sessions";

  @Override
  public String getPath() {
    return PATH;
  }

  @Override
  public String getOptionsJson() {
    // Nothing is exposed to the client (editor) side.
    return "{}";
  }

  @Override
  public String getOptionsForm() {
    List<EditingSessionsRegistry.SessionInfo> sessions = EditingSessionsRegistry.getSessions();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    long now = System.currentTimeMillis();

    Set<String> users = new HashSet<>();
    StringBuilder rows = new StringBuilder();
    for (EditingSessionsRegistry.SessionInfo s : sessions) {
      String user = s.authorName != null && !s.authorName.isEmpty() ? s.authorName : s.userId;
      users.add(String.valueOf(user));
      boolean active = s.state == EditingSessionsRegistry.State.ACTIVE;
      rows.append("<tr>")
        .append("<td><b>").append(esc(user)).append("</b>")
        .append("<div class='sub'>user id: ").append(esc(s.userId))
        .append("<br>licensee: ").append(esc(s.licenseeId)).append("</div></td>")
        .append("<td class='url'>").append(esc(s.systemId)).append("<div class='sub'>")
        .append(esc(s.kind)).append("</div></td>")
        .append("<td>").append(dateFormat.format(new Date(s.startTime))).append("</td>")
        .append("<td>").append(formatLastEdit(s.lastEdit, now, dateFormat)).append("</td>")
        .append("<td><span class='state ").append(active ? "active" : "inactive").append("'>")
        .append(active ? "active" : "idle since " + dateFormat.format(new Date(s.lastActivity)))
        .append("</span></td>")
        .append("</tr>");
    }

    StringBuilder html = new StringBuilder();
    html.append("<!DOCTYPE html><html><head><meta charset='utf-8'>")
      .append("<style>")
      .append("body{font-family:sans-serif;font-size:13px;margin:8px;}")
      .append("table{border-collapse:collapse;width:100%;}")
      .append("th,td{border-bottom:1px solid #ddd;padding:4px 6px;text-align:left;vertical-align:top;}")
      .append("th{background:#f3f3f3;}")
      .append(".url{word-break:break-all;}")
      .append(".sub{color:#777;font-size:11px;}")
      .append(".state.active{color:#2e7d32;}.state.inactive{color:#999;}")
      .append(".toolbar{display:flex;justify-content:space-between;align-items:center;margin-bottom:6px;}")
      .append("</style></head><body>")
      .append("<div id='content'>")
      .append("<div class='toolbar'><span><b>").append(users.size()).append("</b> user(s), <b>")
      .append(sessions.size()).append("</b> open document(s) &mdash; ")
      .append(dateFormat.format(new Date())).append("</span>")
      .append("<button type='button' id='refresh'>Refresh</button></div>");
    if (sessions.isEmpty()) {
      html.append("<p>No documents are open at the moment.</p>");
    } else {
      html.append("<table><thead><tr><th>User</th><th>Document</th><th>Opened at</th><th>Last edit</th><th>State</th>")
        .append("</tr></thead><tbody>").append(rows).append("</tbody></table>");
    }
    html.append("<p class='sub'>Note: if the browser could not notify the server when the document was ")
      .append("closed (browser crash, computer put to sleep, lost network connection), ")
      .append("the session is still listed until it expires.</p>")
      .append("</div>")
      // The script is an external file (web/static/admin-page.js, served by the
      // WebappStaticResourcesFolder extension) because the Administration Page CSP
      // (script-src 'self') blocks inline scripts. The URL is relative to {context}/app/admin.html.
      .append("<script src='../plugin-resources/editing-sessions/admin-page.js'></script>")
      .append("</body></html>");
    return html.toString();
  }

  /**
   * @return The time of the last edit and how long ago it was, or a dash if the document was not
   * modified in this session.
   */
  private static String formatLastEdit(long lastEdit, long now, SimpleDateFormat dateFormat) {
    if (lastEdit <= 0) {
      return "<span class='sub'>&ndash;</span>";
    }
    long minutes = Math.max(0, (now - lastEdit) / 60000);
    String ago;
    if (minutes < 1) {
      ago = "just now";
    } else if (minutes < 60) {
      ago = minutes + " min ago";
    } else if (minutes < 24 * 60) {
      ago = (minutes / 60) + " h " + (minutes % 60) + " min ago";
    } else {
      ago = (minutes / (24 * 60)) + " day(s) ago";
    }
    return "<b>" + ago + "</b><div class='sub'>" + dateFormat.format(new Date(lastEdit)) + "</div>";
  }

  /**
   * Escapes a value for use in HTML text.
   */
  private static String esc(String value) {
    if (value == null) {
      return "";
    }
    StringBuilder sb = new StringBuilder(value.length());
    for (char c : value.toCharArray()) {
      switch (c) {
        case '<': sb.append("&lt;"); break;
        case '>': sb.append("&gt;"); break;
        case '&': sb.append("&amp;"); break;
        case '"': sb.append("&quot;"); break;
        case '\'': sb.append("&#39;"); break;
        default: sb.append(c);
      }
    }
    return sb.toString();
  }
}
