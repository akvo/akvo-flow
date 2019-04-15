var _paq = _paq || [];
_paq.push(['setDocumentTitle', `${document.domain}/${document.title}`]);
_paq.push(['setCookieDomain', '*.akvoflow.org']);
_paq.push(['setDomains', ['*.akvoflow.org']]);
_paq.push(['trackPageView']);
_paq.push(['enableLinkTracking']);

(function () {
  const u = `${(document.location.protocol == 'https:') ? 'https' : 'http'}://analytics.akvo.org/`;
  _paq.push(['setTrackerUrl', `${u}piwik.php`]);
  _paq.push(['setSiteId', '10']);
  const d = document;
  const g = d.createElement('script');
  const s = d.getElementsByTagName('script')[0];
  g.type = 'text/javascript';
  g.defer = true;
  g.async = true;
  g.src = `${u}piwik.js`;
  s.parentNode.insertBefore(g, s);
}());
