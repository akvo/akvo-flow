export function init() {
  window._paq = window._paq || [];

  window._paq.push(['setDocumentTitle', `${document.domain}/${document.title}`]);
  window._paq.push(['setCookieDomain', '*.akvoflow.org']);
  window._paq.push(['setDomains', ['*.akvoflow.org']]);
  window._paq.push(['enableLinkTracking']);

  (function() {
    const u = 'https://analytics.akvo.org/';

    window._paq.push(['setTrackerUrl', `${u}ppms.php`]);
    window._paq.push(['setSiteId', process.env.ANALYTICS_SITE_ID]);

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

const trackEvent = (eventType, ...values) => {
  if (typeof window._paq !== 'undefined') {
    window._paq.push(['trackEvent', eventType, ...values]);
  }
};

const trackPageView = pageTitle => {
  if (typeof window._paq !== 'undefined') {
    const { origin, pathname } = window.location;
    window._paq.push(['setCustomUrl', `${origin}${pathname}`]);
    window._paq.push(['trackPageView', pageTitle]);
  }
};

const SURVEY_TAB = "Survey";
const DEVICES_LIST = "Devices > List";
const DEVICES_ASSIGNMENTS = "Devices > Assignments";
const DEVICES_MANUAL_TRANSFER = "Devices > Manual survey transfer";
const DATA_INSPECT = "Data > Inspect Data";
const DATA_MONITOR = "Data > Monitoring Data";
const DATA_CHARTS = "Data > Charts";
const DATA_EXPORTS = "Data > Exports";
const DATA_BULK_UPLOAD = "Data > Bulk Upload Data";
const DATA_BULK_UPLOAD_IMAGES = "Data > Bulk Upload Images";
const DATA_CLEANING = "Data > Data cleaning";
const RESOURCES_CASCADE = "Resources > Cascade Resources";
const RESOURCES_DATA_APPROVAL = "Resources > Data Approval";
const MAPS = "Maps";
const USERS = "Users List";
const MESSAGES = "Messages";
const STATS = "View Stats"


export { trackEvent, trackPageView, SURVEY_TAB, DEVICES_LIST, DEVICES_ASSIGNMENTS, DEVICES_MANUAL_TRANSFER, DATA_INSPECT, DATA_MONITOR, DATA_CHARTS, DATA_EXPORTS, DATA_BULK_UPLOAD, DATA_BULK_UPLOAD_IMAGES, DATA_CLEANING, RESOURCES_CASCADE, RESOURCES_DATA_APPROVAL, MAPS, USERS, MESSAGES, STATS };
