// create init method
export function init() {
  const u = `${document.location.protocol == 'https:' ? 'https' : 'http'}://analytics.akvo.org/`;
  const d = document;
  const g = d.createElement('script');
  const s = d.getElementsByTagName('script')[0];
  g.type = 'text/javascript';
  g.defer = true;
  g.async = true;
  g.src = `${u}piwik.js`;
  s.parentNode.insertBefore(g, s);

  const _paq = window._paq || [];
  _paq.push(['setDocumentTitle', `${document.domain}/${document.title}`]);
  _paq.push(['setCookieDomain', '*.akvoflow.org']);
  _paq.push(['setDomains', ['*.akvoflow.org']]);
  _paq.push(['trackPageView']);
  _paq.push(['enableLinkTracking']);

  _paq.push(['setTrackerUrl', `${u}piwik.php`]);
  _paq.push(['setSiteId', '10']);
}

export const trackEvent = (eventType, ...values) => {
  if (typeof window._paq !== 'undefined') window._paq.push(['trackEvent', eventType, ...values]);
};

export const trackPageView = pageTitle => {
  if (typeof window._paq !== 'undefined') {
    const { origin, pathname } = window.location;
    window._paq.push(['setCustomUrl', `${origin}${pathname}`]);
    window._paq.push(['trackPageView', pageTitle]);
  }
};

export default { trackEvent, trackPageView };
