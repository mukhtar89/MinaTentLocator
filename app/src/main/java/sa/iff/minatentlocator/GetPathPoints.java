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
    private ArrayList<String[]> places = new ArrayList<>();
    private int j;
    private int count = 0;

    public GetPathPoints(final Context context, final GoogleMap gMap, final String editFrom, final String editTo) {
        this.context = context;
        this.gMap= gMap;
        this.editFrom = editFrom;
        this.editTo = editTo;
        /*places.add(new String[]{"21.397138,39.901807", "21.394452,39.900775"});
        places.add(new String[]{"21.395331,39.903100", "21.395441,39.903004"});
        places.add(new String[]{"21.396080,39.900144", "21.396876,39.901256"});
        places.add(new String[]{"21.392939,39.904108", "21.393148,39.902946"});
        places.add(new String[]{"21.396482,39.902219", "21.394815,39.902741"});
        places.add(new String[]{"21.395692,39.903418", "21.394535,39.905134"});
        places.add(new String[]{"21.394701,39.904665", "21.393840,39.904218"});
        places.add(new String[]{"21.393300,39.903129", "21.394226,39.903691"});
        places.add(new String[]{"21.394886,39.904433", "21.396517,39.906165"});
        places.add(new String[]{"21.390857,39.901879", "21.392855,39.902095"});
        places.add(new String[]{"21.395163,39.900530", "21.391041,39.901975"});
        places.add(new String[]{"21.393813,39.902863", "21.396617,39.901384"});
        places.add(new String[]{"21.401131,39.899259", "21.396863,39.904773"});
        places.add(new String[]{"21.398043,39.902815", "21.394821,39.906481"});
        places.add(new String[]{"21.397259,39.907717", "21.398411,39.904211"});
        places.add(new String[]{"21.398411,39.904211", "21.398335,39.905627"});
        places.add(new String[]{"21.398335,39.905627", "21.399440,39.903133"});
        places.add(new String[]{"21.399440,39.903133", "21.402983,39.897993"});
        places.add(new String[]{"21.402983,39.897993", "21.403072,39.901061"});
        places.add(new String[]{"21.403072,39.901061", "21.401359,39.900448"});
        places.add(new String[]{"21.401359,39.900448", "21.406115,39.900667"});
        places.add(new String[]{"21.406115,39.900667", "21.407654,39.898390"});
        places.add(new String[]{"21.407654,39.898390", "21.405851,39.899828"});
        places.add(new String[]{"21.405851,39.899828", "21.405796,39.897949"});
        places.add(new String[]{"21.405796,39.897949", "21.406414,39.894710"});
        places.add(new String[]{"21.406414,39.894710", "21.403900,39.896102"});
        places.add(new String[]{"21.403900,39.896102", "21.406435,39.891982"});
        places.add(new String[]{"21.406435,39.891982", "21.406333,39.889850"});

        places.add(new String[]{"21.406745,39.898168", "21.404865,39.899030"});
        places.add(new String[]{"21.406611,39.896651", "21.409456,39.892817"});
        places.add(new String[]{"21.409456,39.892817", "21.409374,39.894888"});
        places.add(new String[]{"21.409374,39.894888", "21.408181,39.895488"});
        places.add(new String[]{"21.408181,39.895488", "21.409332,39.891788"});
        places.add(new String[]{"21.409332,39.891788", "21.407612,39.892814"});
        places.add(new String[]{"21.407612,39.892814", "21.408362,39.891083"});
        places.add(new String[]{"21.408362,39.891083", "21.406451,39.894370"});
        places.add(new String[]{"21.406451,39.894370", "21.404414,39.893458"});
        places.add(new String[]{"21.404414,39.893458", "21.405745,39.891024"});
        places.add(new String[]{"21.405745,39.891024", "21.405128,39.889396"});
        places.add(new String[]{"21.405128,39.889396", "21.405474,39.892913"});
        places.add(new String[]{"21.405474,39.892913", "21.402690,39.895450"});
        places.add(new String[]{"21.402690,39.895450", "21.408339,39.894431"});

        places.add(new String[]{"21.406166, 39.901260", "21.403501, 39.910246"});
        places.add(new String[]{"21.403501, 39.910246", "21.407016, 39.907954"});
        places.add(new String[]{"21.407016, 39.907954", "21.407197, 39.909083"});
        places.add(new String[]{"21.407197, 39.909083", "21.411460, 39.907890"});
        places.add(new String[]{"21.411460, 39.907890", "21.404423, 39.910608"});
        places.add(new String[]{"21.404423, 39.910608", "21.405830, 39.910598"});
        places.add(new String[]{"21.405830, 39.910598", "21.407455, 39.909296"});
        places.add(new String[]{"21.407455, 39.909296", "21.406235, 39.907715"});
        places.add(new String[]{"21.406235, 39.907715", "21.411698, 39.909401"});
        places.add(new String[]{"21.411698, 39.909401", "21.410675, 39.906004"});
        places.add(new String[]{"21.410675, 39.906004", "21.409874, 39.904795"});
        places.add(new String[]{"21.409874, 39.904795", "21.410145, 39.903484"});
        places.add(new String[]{"21.410145, 39.903484", "21.408156, 39.904143"});
        places.add(new String[]{"21.408156, 39.904143", "21.410103, 39.902240"});
        places.add(new String[]{"21.410103, 39.902240", "21.407575, 39.903374"});
        places.add(new String[]{"21.407575, 39.903374", "21.408400, 39.901049"});
        places.add(new String[]{"21.408400, 39.901049", "21.406498, 39.901747"});
        places.add(new String[]{"21.406498, 39.901747", "21.409235, 39.899191"});
        places.add(new String[]{"21.409235, 39.899191", "21.409594, 39.900359"});
        places.add(new String[]{"21.409594, 39.900359", "21.410164, 39.901165"});
        places.add(new String[]{"21.410164, 39.901165", "21.411277, 39.902862"});

        places.add(new String[]{"21.406863, 39.901442", "21.410169, 39.906407"});
        places.add(new String[]{"21.410169, 39.906407", "21.410268, 39.905321"});
        places.add(new String[]{"21.410268, 39.905321", "21.411640, 39.907997"});
        places.add(new String[]{"21.411640, 39.907997", "21.407369, 39.909865"});
        places.add(new String[]{"21.407369, 39.909865", "21.411105, 39.907407"});
        places.add(new String[]{"21.411105, 39.907407", "21.411656, 39.909439"});
        places.add(new String[]{"21.411656, 39.909439", "21.413192, 39.908986"});
        places.add(new String[]{"21.413192, 39.908986", "21.410649, 39.905741"});
        places.add(new String[]{"21.410649, 39.905741", "21.417149, 39.901962"});
        places.add(new String[]{"21.417149, 39.901962", "21.413604, 39.900966"});
        places.add(new String[]{"21.413604, 39.900966", "21.417433, 39.900021"});
        places.add(new String[]{"21.417433, 39.900021", "21.415163, 39.898908"});
        places.add(new String[]{"21.415163, 39.898908", "21.417602, 39.898550"});
        places.add(new String[]{"21.417602, 39.898550", "21.413314, 39.900519"});

        places.add(new String[]{"21.413314, 39.900519", "21.414708, 39.896599"});
        places.add(new String[]{"21.414708, 39.896599", "21.417480, 39.896621"});
        places.add(new String[]{"21.417480, 39.896621", "21.413275, 39.900540"});
        places.add(new String[]{"21.413275, 39.900540", "21.417422, 39.899983"});
        places.add(new String[]{"21.417422, 39.899983", "21.419485, 39.901655"});
        places.add(new String[]{"21.419485, 39.901655", "21.419887, 39.898609"});
        places.add(new String[]{"21.419887, 39.898609", "21.417963, 39.896861"});
        places.add(new String[]{"21.417963, 39.896861", "21.419224, 39.895930"});
        places.add(new String[]{"21.419224, 39.895930", "21.417846, 39.893586"});
        places.add(new String[]{"21.417846, 39.893586", "21.420837, 39.894988"});
        places.add(new String[]{"21.420837, 39.894988", "21.415615, 39.894756"});
        places.add(new String[]{"21.415615, 39.894756", "21.420146, 39.893992"});
        places.add(new String[]{"21.420146, 39.893992", "21.414889, 39.893763"});
        places.add(new String[]{"21.414889, 39.893763", "21.415671, 39.891628"});
        places.add(new String[]{"21.415671, 39.891628", "21.418485, 39.895544"});
        places.add(new String[]{"21.418485, 39.895544", "21.419885, 39.896967"});
        places.add(new String[]{"21.419885, 39.896967", "21.421314, 39.900705"});
        places.add(new String[]{"21.421314, 39.900705", "21.419881, 39.898646"});
        places.add(new String[]{"21.419881, 39.898646", "21.417919, 39.901944"});
        places.add(new String[]{"21.417919, 39.901944", "21.422424, 39.896647"});
        places.add(new String[]{"21.422424, 39.896647", "21.425600, 39.898277"});
        places.add(new String[]{"21.425600, 39.898277", "21.423338, 39.898099"});
        places.add(new String[]{"21.423338, 39.898099", "21.422778, 39.900709"});

        places.add(new String[]{"21.423338, 39.898099", "21.423908, 39.892648"});
        places.add(new String[]{"21.423908, 39.892648", "21.417787, 39.896095"});
        places.add(new String[]{"21.417787, 39.896095", "21.418201, 39.878535"});
        places.add(new String[]{"21.418201, 39.878535", "21.409028, 39.890829"});
        places.add(new String[]{"21.409028, 39.890829", "21.402506, 39.894940"});
        places.add(new String[]{"21.402506, 39.894940", "21.413086, 39.894060"});
        places.add(new String[]{"21.413086, 39.894060", "21.415657, 39.889081"});
        places.add(new String[]{"21.415657, 39.889081", "21.417384, 39.878193"});
        places.add(new String[]{"21.417384, 39.878193", "21.413434, 39.893461"});
        places.add(new String[]{"21.413434, 39.893461", "21.415467, 39.875008"});
        places.add(new String[]{"21.415467, 39.875008", "21.405589, 39.889368"});
        places.add(new String[]{"21.405589, 39.889368", "21.413421, 39.888679"});
        places.add(new String[]{"21.413421, 39.888679", "21.418500, 39.881362"});
        places.add(new String[]{"21.418500, 39.881362", "21.391974, 39.902764"});
        places.add(new String[]{"21.391974, 39.902764", "21.412993, 39.908964"});
        places.add(new String[]{"21.412993, 39.908964", "21.425359, 39.894382"});
        places.add(new String[]{"21.425359, 39.894382", "21.410516, 39.884982"});
        places.add(new String[]{"21.410516, 39.884982", "21.418769, 39.871556"});
        places.add(new String[]{"21.418769, 39.871556", "21.414528, 39.896487"});
        places.add(new String[]{"21.414528, 39.896487", "21.405946, 39.901886"});

        places.add(new String[]{"21.414528, 39.896487", "21.426193, 39.869716"});
        places.add(new String[]{"21.426193, 39.869716", "21.422381, 39.876073"});
        places.add(new String[]{"21.422381, 39.876073", "21.425809, 39.873320"});
        places.add(new String[]{"21.425809, 39.873320", "21.419624, 39.880616"});
        places.add(new String[]{"21.419624, 39.880616", "21.420463, 39.884234"});
        places.add(new String[]{"21.420463, 39.884234", "21.416529, 39.888149"});
        places.add(new String[]{"21.416529, 39.888149", "21.416901, 39.891655"});
        places.add(new String[]{"21.416901, 39.891655", "21.427079, 39.893796"});
        places.add(new String[]{"21.427079, 39.893796", "21.404299, 39.888625"});
        places.add(new String[]{"21.404299, 39.888625", "21.410602, 39.903468"});
        places.add(new String[]{"21.410602, 39.903468", "21.395762, 39.907208"});
        places.add(new String[]{"21.395762, 39.907208", "21.408708, 39.894188"});
        places.add(new String[]{"21.408708, 39.894188", "21.418389, 39.884232"});
        places.add(new String[]{"21.418389, 39.884232", "21.405559, 39.888877"});
        places.add(new String[]{"21.405559, 39.888877", "21.404837, 39.901652"});
        places.add(new String[]{"21.404837, 39.901652", "21.414551, 39.879762"});
        places.add(new String[]{"21.414551, 39.879762", "21.411333, 39.892166"});

        places.add(new String[]{"21.406137, 39.900705", "21.403814, 39.897827"});
        places.add(new String[]{"21.403814, 39.897827", "21.406676, 39.898648"});
        places.add(new String[]{"21.406676, 39.898648", "21.405396, 39.898720"});
        places.add(new String[]{"21.405396, 39.898720", "21.426104, 39.900890"});
        places.add(new String[]{"21.426104, 39.900890", "21.417272, 39.885425"});
        places.add(new String[]{"21.417272, 39.885425", "21.410469, 39.897506"});
        places.add(new String[]{"21.410469, 39.897506", "21.409649, 39.895509"});
        places.add(new String[]{"21.409649, 39.895509", "21.410958, 39.894961"});
        places.add(new String[]{"21.410958, 39.894961", "21.412507, 39.899342"});*/
        places.add(new String[]{"21.412507, 39.899342", "21.417213, 39.881500"});
        places.add(new String[]{"21.417213, 39.881500", "21.415870, 39.880819"});
        places.add(new String[]{"21.415870, 39.880819", "21.417576, 39.882241"});
        places.add(new String[]{"21.417576, 39.882241", "21.417293, 39.878115"});
        places.add(new String[]{"21.417293, 39.878115", "21.408560, 39.890486"});
        places.add(new String[]{"21.408560, 39.890486", "21.409816, 39.885331"});
        places.add(new String[]{"21.409816, 39.885331", "21.391784, 39.902779"});

        places.add(new String[]{"21.411053, 39.892669", "21.408477, 39.897525"});
        places.add(new String[]{"21.410254, 39.894180", "21.410945, 39.895114"});
        places.add(new String[]{"21.410909, 39.895996", "21.409373, 39.895894"});
        places.add(new String[]{"21.409373, 39.895894", "21.410827, 39.897956"});

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
            new RotaTask(context, gMap, editTo, editFrom).showPath();
        }
    }
}

