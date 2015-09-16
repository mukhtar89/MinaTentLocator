package sa.iff.minatentlocator;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Mukhtar on 8/26/2015.
 */
public class GetPathPoints extends AsyncTask<Void, Integer, Boolean> {

    private static final String TOAST_MSG = "Getting Path Points";
    private static final String TOAST_ERR_MAJ = "Impossible to trace Itinerary";
    private Context context;
    private GoogleMap gMap;
    private String editFrom;
    private String editTo;
    private ArrayList<String[]> places = new ArrayList<>();

    public GetPathPoints(final Context context, final GoogleMap gMap, final String editFrom, final String editTo) {
        this.context = context;
        this.gMap= gMap;
        this.editFrom = editFrom;
        this.editTo = editTo;
        places.add(new String[]{"21.397138,39.901807", "21.394452,39.900775"});
        places.add(new String[]{"21.395331,39.903100", "21.395441,39.903004"});
        places.add(new String[]{"21.396080,39.900144", "21.396876,39.901256"});

        places.add(new String[]{"21.392939,39.904108", "21.393148,39.902946"});
        places.add(new String[]{"21.396482,39.902219", "21.394815,39.902741"});
        places.add(new String[]{"21.395692,39.903418", "21.394535,39.905134"});

        places.add(new String[]{"21.394701,39.904665", "21.393840,39.904218"});
        /*places.add(new String[]{"21.396482,39.902219", "21.394815,39.902741"});
        places.add(new String[]{"21.395692,39.903418", "21.394535,39.905134"});
        places.add(new String[]{"21.392939,39.904108", "21.393148,39.902946"});
        places.add(new String[]{"21.396482,39.902219", "21.394815,39.902741"});
        places.add(new String[]{"21.395692,39.903418", "21.394535,39.905134"});*/
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(context, TOAST_MSG, Toast.LENGTH_LONG).show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            for (int j = 0; j < places.size(); j++) {
                final StringBuilder url = new StringBuilder("http://maps.googleapis.com/maps/api/directions/xml?language=eng&mode=driving");
                url.append("&origin=");
                url.append(places.get(j)[0].replace(' ', '+'));
                url.append("&destination=");
                url.append(places.get(j)[1].replace(' ', '+'));

                final InputStream stream = new URL(url.toString()).openStream();
                final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                documentBuilderFactory.setIgnoringComments(true);
                final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                final Document document = documentBuilder.parse(stream);
                document.getDocumentElement().normalize();

                final String status = document.getElementsByTagName("status").item(0).getTextContent();
                if (!"OK".equals(status)) {
                    return false;
                }
                final Element elementLeg = (Element) document.getElementsByTagName("leg").item(0);
                final NodeList nodeListStep = elementLeg.getElementsByTagName("step");
                final int length = nodeListStep.getLength();
                for (int i = 0; i < length; i++) {
                    final Node nodeStep = nodeListStep.item(i);
                    if (nodeStep.getNodeType() == Node.ELEMENT_NODE) {
                        final Element elementStep = (Element) nodeStep;
                        decodePolylines(elementStep.getElementsByTagName("points").item(0).getTextContent());
                    }
                }
            }
        }catch(final Exception e){
            return false;
        }
        return true;
    }

    private void decodePolylines(final String encodedPoints) {
        int index = 0;
        int lat = 0, lng = 0;
        LatLng temp1, temp2 = null;
        while (index < encodedPoints.length()) {
            int b, shift = 0, result = 0;
            do {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift; shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat; shift = 0;
            result = 0;
            do {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            temp1 = new LatLng((double)lat/1E5, (double)lng/1E5);
            GraphMaker.add(temp1, temp2);
            temp2 = temp1;
        }
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        if (!result)
            Toast.makeText(context, TOAST_ERR_MAJ, Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(context, TOAST_MSG, Toast.LENGTH_LONG).show();
            new RotaTask(context, gMap, editTo, editFrom).showPath();
        }
    }
}

