package sa.iff.minatentlocator;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
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
public class GetPathPoints {

    private static final String TOAST_MSG = "Getting Path Points";
    private static final String TOAST_ERR_MAJ = "Impossible to trace Itinerary";
    private Context context;
    private GoogleMap gMap;
    private String editFrom;
    private String editTo;
    private String place;
    private ArrayList<String[]> places = new ArrayList<>();
    private int j;
    private int count = 0;

    public GetPathPoints(final Context context, final GoogleMap gMap, final String editFrom, final String editTo, final String place) {
        this.context = context;
        this.gMap= gMap;
        this.editFrom = editFrom;
        this.editTo = editTo;
        this.place = place;
        /*places.add(new String[]{"21.434911, 39.831132", "21.354856, 39.984101"});
        places.add(new String[]{"21.421801, 39.871239", "21.433574, 39.829449"});
        places.add(new String[]{"21.434123, 39.831134", "21.377200, 39.849669"});
        places.add(new String[]{"21.434123, 39.831134", "21.438187, 39.832348"});
        places.add(new String[]{"21.437374, 39.831403", "21.429577, 39.812967"});
        places.add(new String[]{"21.426292, 39.810306", "21.426402, 39.812745"});
        places.add(new String[]{"21.425565, 39.819225", "21.419532, 39.833211"});
        places.add(new String[]{"21.413475, 39.827676", "21.425103, 39.824676"});
        places.add(new String[]{"21.424995, 39.828409", "21.419538, 39.827240"});
        places.add(new String[]{"21.419825, 39.828407", "21.420410, 39.829759"});
        places.add(new String[]{"21.420452, 39.830196", "21.420547, 39.830244"});
        places.add(new String[]{"21.421183, 39.832399", "21.421298, 39.832812"});
        places.add(new String[]{"21.421717, 39.832809", "21.421719, 39.834523"});
        places.add(new String[]{"21.422485, 39.834084", "21.420023, 39.833814"});
        places.add(new String[]{"21.419618, 39.833352", "21.419571, 39.833380"});
        places.add(new String[]{"21.419067, 39.826534", "21.418793, 39.826774"});*/

        /*places.add(new String[]{"21.419262, 39.821845", "21.412833, 39.823771"});
        places.add(new String[]{"21.412906, 39.823599", "21.413479, 39.821406"});
        places.add(new String[]{"21.438698, 39.820858", "21.436796, 39.820225"});
        places.add(new String[]{"21.434412, 39.829391", "21.424374, 39.816100"});
        places.add(new String[]{"21.433753, 39.829128", "21.434412, 39.829391"});
        places.add(new String[]{"21.424408, 39.816347", "21.424416, 39.812966"});
        places.add(new String[]{"21.423373, 39.808906", "21.426845, 39.814013"});
        places.add(new String[]{"21.377200, 39.849669", "21.434934, 39.820455"});
        places.add(new String[]{"21.423036, 39.807970", "21.426845, 39.814013"});*/

        /*places.add(new String[]{"21.426933, 39.813810", "21.423146, 39.808512"});
        places.add(new String[]{"21.424416, 39.812966", "21.437203, 39.820968"});
        places.add(new String[]{"21.435690, 39.820501", "21.424208, 39.811432"});
        places.add(new String[]{"21.439690, 39.852434", "21.426933, 39.813810"});
        places.add(new String[]{"21.436796, 39.820225", "21.424356, 39.812614"});
        places.add(new String[]{"21.423036, 39.807970", "21.434786, 39.819583"});
        places.add(new String[]{"21.434779, 39.820739", "21.424343, 39.815522"});
        places.add(new String[]{"21.496901, 39.793810", "21.420696, 39.831452"});
        places.add(new String[]{"21.419966, 39.827913", "21.420303, 39.829090"});
        places.add(new String[]{"21.420452, 39.830196", "21.421183, 39.832399"});
        places.add(new String[]{"21.419825, 39.828407", "21.420983, 39.832031"});
        places.add(new String[]{"21.457038, 39.859174", "21.456189, 39.861416"});
        places.add(new String[]{"21.494845, 39.794110", "21.441101, 39.811470"});*/
        /*places.add(new String[]{"21.437326, 39.824666", "21.377298, 39.849729"});

        places.add(new String[]{"21.422470, 39.826192", "21.423690, 39.819061"});
        places.add(new String[]{"21.421151, 39.824128", "21.423127, 39.833474"});
        places.add(new String[]{"21.425103, 39.824676", "21.434720, 39.828625"});
        places.add(new String[]{"21.436807, 39.827177", "21.423525, 39.827385"});
        places.add(new String[]{"21.423525, 39.827385", "21.421058, 39.821810"});
        places.add(new String[]{"21.441960, 39.807854", "21.429088, 39.830498"});
        places.add(new String[]{"21.429088, 39.830498", "21.422652, 39.821325"});
        places.add(new String[]{"21.425012, 39.829964", "21.423880, 39.830417"});*/
        places.add(new String[]{"21.423880, 39.830417", "21.411585, 39.820287"});
        places.add(new String[]{"21.411585, 39.820287", "21.411341, 39.822995"});
        places.add(new String[]{"21.421703, 39.834473", "21.411518, 39.822108"});
        places.add(new String[]{"21.421703, 39.834473", "21.420522, 39.832751"});
        places.add(new String[]{"21.420522, 39.832751", "21.422608, 39.834165"});
        places.add(new String[]{"21.422608, 39.834165", "21.425808, 39.813291"});
        places.add(new String[]{"21.425808, 39.813291", "21.422470, 39.826192"});
        places.add(new String[]{"21.422470, 39.826192", "21.496901, 39.793810"});
        places.add(new String[]{"21.496901, 39.793810", "21.417752, 39.827525"});
        places.add(new String[]{"21.417752, 39.827525", "21.419432, 39.833299"});
        places.add(new String[]{"21.419432, 39.833299", "21.421583, 39.834379"});
        places.add(new String[]{"21.421462, 39.834883", "21.426690, 39.815162"});
        places.add(new String[]{"21.426690, 39.815162", "21.427586, 39.816541"});





    }

    public void execute() {
        Toast.makeText(context, TOAST_MSG, Toast.LENGTH_LONG).show();
            for (j = 0; j < places.size(); j++) {
                new AsyncTask<String, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(String... params) {
                        try {
                            final StringBuilder url = new StringBuilder("http://maps.googleapis.com/maps/api/directions/xml?language=eng&mode=walking");
                            url.append("&origin=");
                            url.append(params[0].replace(' ', '+'));
                            url.append("&destination=");
                            url.append(params[1].replace(' ', '+'));

                            final InputStream stream = new URL(url.toString()).openStream();
                            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                            documentBuilderFactory.setIgnoringComments(true);
                            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                            final Document document = documentBuilder.parse(stream);
                            document.getDocumentElement().normalize();

                            final String status = document.getElementsByTagName("status").item(0).getTextContent();
                            if (!"OK".equals(status)) {
                                return -j;
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
                        } catch (final Exception e) {
                            return -j;
                        }
                        return j;
                    }

                    @Override
                    protected void onPostExecute(Integer result) {
                        super.onPostExecute(result);
                        collector(result);
                    }
                }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, places.get(j));
            }
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

    protected synchronized void collector(final Integer result) {
        count++;
        if (result < 0) {
            Toast.makeText(context, TOAST_ERR_MAJ + j, Toast.LENGTH_SHORT).show();
            Log.d("Wrong direction", result.toString());
        }
        else if (count == places.size()){
            Toast.makeText(context, TOAST_MSG, Toast.LENGTH_LONG).show();
            new RotaTask(context, gMap, editTo, editFrom, place).showPath();
        }
    }
}

