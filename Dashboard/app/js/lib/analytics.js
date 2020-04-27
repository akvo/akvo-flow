export function init() {
  window._paq = window._paq || [];

  window._paq.push(['setDocumentTitle', `${document.domain}/${document.title}`]);
  window._paq.push(['setCookieDomain', '*.akvoflow.org']);
  window._paq.push(['setDomains', ['*.akvoflow.org']]);
  window._paq.push(['enableLinkTracking']);

  (function() {
    const u = 'https://akvo.piwikpro.com/';

    window._paq.push(['setTrackerUrl', `${u}ppms.php`]);
    window._paq.push(['setSiteId', '2eb02fff-08a4-4973-ae92-fb4ae6157da4']);

    const d = document;
    const g = d.createElement('script');
    const s = d.getElementsByTagName('script')[0];
    g.type = 'text/javascript';
    g.async = true;
    g.defer = true;
    g.src = `${u}ppms.js`;
    s.parentNode.insertBefore(g, s);
  })();
}

export const trackEvent = (eventType, ...values) => {
  if (typeof window._paq !== 'undefined') {
    window._paq.push(['trackEvent', eventType, ...values]);
  }
};

export const trackPageView = pageTitle => {
  if (typeof window._paq !== 'undefined') {
    const { origin, pathname } = window.location;
    window._paq.push(['setCustomUrl', `${origin}${pathname}`]);
    window._paq.push(['trackPageView', pageTitle]);
  }
};

export default { trackEvent, trackPageView };
