package org.waterforpeople.mapping.app.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.dao.KMLDAO;
import org.waterforpeople.mapping.helper.KMLHelper;

import com.gallatinsystems.common.util.ZipUtil;
import com.gallatinsystems.gis.map.dao.MapFragmentDao;
import com.gallatinsystems.gis.map.domain.MapFragment;
import com.gallatinsystems.gis.map.domain.MapFragment.FRAGMENTTYPE;
import com.google.appengine.api.datastore.Blob;

@SuppressWarnings("serial")
public class WaterForPeopleMappingGoogleServlet extends HttpServlet {
	@SuppressWarnings("unused")
	private static final Logger log = Logger
			.getLogger(WaterForPeopleMappingGoogleServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String showKML = req.getParameter("showKML");
		@SuppressWarnings("unused")
		String processFile = req.getParameter("processFile");
		String showRegion = req.getParameter("showRegion");
		String action = req.getParameter("action");
		if (showKML != null) {
			Long kmlKey = null;
			if (req.getParameter("kmlID") != null) {
				kmlKey = Long.parseLong(req.getParameter("kmlID"));
			}
			if (kmlKey != null) {
				KMLDAO kmlDAO = new KMLDAO();
				String kmlString = kmlDAO.getKML(kmlKey);
				resp.setContentType("application/vnd.google-earth.kml+xml");
				resp.getWriter().println(kmlString);
			} else {
				KMLGenerator kmlGen = new KMLGenerator();
				String placemarksDocument = kmlGen
						.generateDocument("PlacemarkTabs.vm");
				// ToDo implement kmz compression now that kmls are so big
				// application/vnd.google-earth.kmz
				resp.setContentType("application/vnd.google-earth.kmz+xml");
				ServletOutputStream out = resp.getOutputStream();
				resp.setHeader("Content-Disposition",
						"inline; filename=waterforpeoplemapping.kmz;");
				ByteArrayOutputStream os = ZipUtil
						.generateZip(placemarksDocument);
				out.write(os.toByteArray());
				out.flush();

			}
		} else if (showRegion != null) {
			KMLGenerator kmlGen = new KMLGenerator();
			String placemarksDocument = kmlGen
					.generateRegionDocumentString("Regions.vm");
			resp.setContentType("application/vnd.google-earth.kml+xml");
			resp.getWriter().println(placemarksDocument);
		}else if("buildMap".equals(action)){
			KMLHelper kmlHelper = new KMLHelper();
			MapFragmentDao mfDao = new MapFragmentDao();
			kmlHelper.buildMap();
			List<MapFragment> mfList = mfDao.searchMapFragments(null,null,null,null, FRAGMENTTYPE.GLOBAL_ALL_PLACEMARKS, "all");
			Blob map = mfList.get(0).getBlob();
			resp.setContentType("application/vnd.google-earth.kmz+xml");
			ServletOutputStream out = resp.getOutputStream();
			resp.setHeader("Content-Disposition",
					"inline; filename=waterforpeoplemapping.kmz;");
			out.write(map.getBytes());
			out.flush();
		}
	}

}
