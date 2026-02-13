package com.example.routeservice.proxy;

import com.example.routeservice.dto.DistanceGroupResponse;
import com.example.routeservice.dto.DistanceSumResponse;
import com.example.routeservice.dto.FilteredRoutesResponse;
import com.example.routeservice.dto.RouteCreateRequest;
import com.example.routeservice.dto.RouteListResponse;
import com.example.routeservice.dto.RouteUpdateRequest;
import com.example.routeservice.entity.Route;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

@Path("/routes")
@Produces({"application/xml"})
@Consumes({"application/xml"})
@ApplicationScoped
public class RouteProxyResource {
    private String soapServiceUrl;
    private CloseableHttpClient httpClient;
    @Context
    private UriInfo uriInfo;

    @PostConstruct
    public void init() {
        this.soapServiceUrl = System.getenv("SOAP_SERVICE_URL");
        if (this.soapServiceUrl == null || this.soapServiceUrl.isEmpty()) {
            this.soapServiceUrl = "https://localhost:38443/route-web/RouteSoapService";
        }

        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init((KeyManager[])null, trustAllCerts, new SecureRandom());
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sc, (hostname, session) -> true);
            this.httpClient = HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize HTTP client: " + e.getMessage(), e);
        }
    }

    private String callSoapService(String soapBody) throws Exception {
        HttpPost httpPost = new HttpPost(this.soapServiceUrl);
        httpPost.setHeader("Content-Type", "text/xml; charset=utf-8");
        httpPost.setHeader("SOAPAction", "");
        httpPost.setEntity(new StringEntity(soapBody, "UTF-8"));
        HttpResponse httpResponse = this.httpClient.execute(httpPost);
        return EntityUtils.toString(httpResponse.getEntity());
    }

    private RouteListResponse callSoapGetRoutes(Integer page, Integer size, String[] sort, Map<String, String> filterParams) {
        try {
            String soapBody = this.buildGetRoutesSoapRequest(page, size, sort, filterParams);
            String response = this.callSoapService(soapBody);
            return this.parseGetRoutesResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to call SOAP getRoutes: " + e.getMessage(), e);
        }
    }

    private String buildGetRoutesSoapRequest(Integer page, Integer size, String[] sort, Map<String, String> filterParams) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">");
        sb.append("<soap:Body>");
        sb.append("<getRoutes xmlns=\"http://routeservice.example.com/soap\">");
        if (page != null) {
            sb.append("<page xmlns=\"\">").append(page).append("</page>");
        }

        if (size != null) {
            sb.append("<size xmlns=\"\">").append(size).append("</size>");
        }

        if (sort != null) {
            for (String s : sort) {
                sb.append("<sort xmlns=\"\">").append(s).append("</sort>");
            }
        }

        if (filterParams != null && !filterParams.isEmpty()) {
            sb.append("<filterParams xmlns=\"\">");

            for (Map.Entry<String, String> entry : filterParams.entrySet()) {
                sb.append("<entry><key>").append(entry.getKey()).append("</key><value>").append(entry.getValue()).append("</value></entry>");
            }

            sb.append("</filterParams>");
        }

        sb.append("</getRoutes>");
        sb.append("</soap:Body>");
        sb.append("</soap:Envelope>");
        return sb.toString();
    }

    private String extractSoapBodyContent(String soapResponse, String tagName) {
        String startTag = "<" + tagName;
        String endTag = "</" + tagName + ">";
        int startIndex = soapResponse.indexOf(startTag);
        if (startIndex == -1) {
            startTag = "<ns2:" + tagName;
            startIndex = soapResponse.indexOf(startTag);
        }

        if (startIndex == -1) {
            startTag = "<ns3:" + tagName;
            startIndex = soapResponse.indexOf(startTag);
        }

        int endIndex = soapResponse.indexOf(endTag, startIndex);
        if (endIndex == -1) {
            endTag = "</ns2:" + tagName + ">";
            endIndex = soapResponse.indexOf(endTag, startIndex);
        }

        if (endIndex == -1) {
            endTag = "</ns3:" + tagName + ">";
            endIndex = soapResponse.indexOf(endTag, startIndex);
        }

        if (startIndex != -1 && endIndex != -1) {
            int contentStart = soapResponse.indexOf(">", startIndex) + 1;
            return soapResponse.substring(contentStart, endIndex).trim();
        } else {
            return null;
        }
    }

    private RouteListResponse parseGetRoutesResponse(String xml) {
        try {
            String content = this.extractSoapBodyContent(xml, "routeListResponse");
            if (content != null && !content.trim().isEmpty()) {
                content = content.replaceAll(" xmlns=\"[^\"]*\"", "").replaceAll(" xmlns:ns\\d+=\"[^\"]*\"", "").replaceAll(" xmlns:ns=\"[^\"]*\"", "");
                String responseXml = "<RouteListResponse>" + content + "</RouteListResponse>";
                JAXBContext jaxbContext = JAXBContext.newInstance(RouteListResponse.class, Route.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                return (RouteListResponse) unmarshaller.unmarshal(new StringReader(responseXml));
            } else {
                return new RouteListResponse();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse SOAP response: " + e.getMessage(), e);
        }
    }

    @GET
    public Response getRoutes(@QueryParam("page") @DefaultValue("0") Integer page, @QueryParam("size") @DefaultValue("20") Integer size, @QueryParam("sort") String[] sort) {
        Map<String, String> filterParams = this.uriInfo.getQueryParameters().entrySet().stream().filter(entry -> entry.getKey().startsWith("filter.")).collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(0)));
        RouteListResponse response = this.callSoapGetRoutes(page, size, sort, filterParams);
        return Response.ok(response).build();
    }

    @POST
    public Response createRoute(RouteCreateRequest request) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(RouteCreateRequest.class, Route.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty("jaxb.fragment", true);
            StringWriter sw = new StringWriter();
            marshaller.marshal(request, sw);
            String requestXml = sw.toString();
            requestXml = requestXml.replaceAll("xmlns=\"http://routeservice.example.com/soap\"", "xmlns=\"\"").replaceAll("xmlns='http://routeservice.example.com/soap'", "xmlns=''");
            int reqStartIdx = requestXml.indexOf("<RouteCreateRequest");
            if (reqStartIdx >= 0) {
                int reqContentStart = requestXml.indexOf(">", reqStartIdx) + 1;
                int reqEndIdx = requestXml.lastIndexOf("</RouteCreateRequest>");
                if (reqEndIdx > reqContentStart) {
                    String content = requestXml.substring(reqContentStart, reqEndIdx).trim();
                    requestXml = content;
                }
            }

            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><createRoute xmlns=\"http://routeservice.example.com/soap\"><request xmlns=\"\">" + requestXml + "</request></createRoute></soap:Body></soap:Envelope>";
            String response = this.callSoapService(soapBody);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            int startIdx = response.indexOf("<route");
            int endIdx = response.lastIndexOf("</route>") + 8;
            if (startIdx >= 0 && endIdx > startIdx) {
                String routeXml = response.substring(startIdx, endIdx);
                Route createdRoute = (Route) unmarshaller.unmarshal(new StringReader(routeXml));
                URI location = this.uriInfo.getAbsolutePathBuilder().path(createdRoute.getId().toString()).build();
                return Response.created(location).entity(createdRoute).build();
            } else {
                throw new RuntimeException("Invalid SOAP response. Response: " + response.substring(0, Math.min(500, response.length())));
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = e.getClass().getName() + ": " + (e.getCause() != null ? e.getCause().getMessage() : "unknown error");
            }
            throw new RuntimeException("Failed to call SOAP service: " + errorMsg, e);
        }
    }

    @GET
    @Path("/{id}")
    public Response getRouteById(@PathParam("id") Long id) {
        try {
            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><getRouteById xmlns=\"http://routeservice.example.com/soap\"><id xmlns=\"\">" + id + "</id></getRouteById></soap:Body></soap:Envelope>";
            String response = this.callSoapService(soapBody);
            JAXBContext jaxbContext = JAXBContext.newInstance(Route.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            int startIdx = response.indexOf("<route");
            int endIdx = response.lastIndexOf("</route>") + 8;
            if (startIdx >= 0 && endIdx > startIdx) {
                String routeXml = response.substring(startIdx, endIdx);
                Route route = (Route) unmarshaller.unmarshal(new StringReader(routeXml));
                return Response.ok(route).build();
            } else {
                throw new RuntimeException("Invalid SOAP response. Response: " + response.substring(0, Math.min(500, response.length())));
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = e.getClass().getName() + ": " + (e.getCause() != null ? e.getCause().getMessage() : "unknown error");
            }
            throw new RuntimeException("Failed to call SOAP service: " + errorMsg, e);
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateRoute(@PathParam("id") Long id, RouteUpdateRequest request) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(RouteUpdateRequest.class, Route.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty("jaxb.fragment", true);
            StringWriter sw = new StringWriter();
            marshaller.marshal(request, sw);
            String requestXml = sw.toString();
            requestXml = requestXml.replaceAll("xmlns=\"http://routeservice.example.com/soap\"", "xmlns=\"\"").replaceAll("xmlns='http://routeservice.example.com/soap'", "xmlns=''");
            int reqStartIdx = requestXml.indexOf("<RouteUpdateRequest");
            if (reqStartIdx >= 0) {
                int reqContentStart = requestXml.indexOf(">", reqStartIdx) + 1;
                int reqEndIdx = requestXml.lastIndexOf("</RouteUpdateRequest>");
                if (reqEndIdx > reqContentStart) {
                    String content = requestXml.substring(reqContentStart, reqEndIdx).trim();
                    requestXml = content;
                }
            }

            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><updateRoute xmlns=\"http://routeservice.example.com/soap\"><id xmlns=\"\">" + id + "</id><request xmlns=\"\">" + requestXml.replaceAll("xmlns=\"http://routeservice.example.com/soap\"", "xmlns=\"\"").replaceAll("xmlns='http://routeservice.example.com/soap'", "xmlns=''") + "</request></updateRoute></soap:Body></soap:Envelope>";
            String response = this.callSoapService(soapBody);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            int startIdx = response.indexOf("<route");
            int endIdx = response.lastIndexOf("</route>") + 8;
            if (startIdx >= 0 && endIdx > startIdx) {
                String routeXml = response.substring(startIdx, endIdx);
                Route updatedRoute = (Route) unmarshaller.unmarshal(new StringReader(routeXml));
                return Response.ok(updatedRoute).build();
            } else {
                throw new RuntimeException("Invalid SOAP response. Response: " + response.substring(0, Math.min(500, response.length())));
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = e.getClass().getName() + ": " + (e.getCause() != null ? e.getCause().getMessage() : "unknown error");
            }
            throw new RuntimeException("Failed to call SOAP service: " + errorMsg, e);
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRoute(@PathParam("id") Long id) {
        try {
            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><deleteRoute xmlns=\"http://routeservice.example.com/soap\"><id xmlns=\"\">" + id + "</id></deleteRoute></soap:Body></soap:Envelope>";
            this.callSoapService(soapBody);
            return Response.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = e.getClass().getName() + ": " + (e.getCause() != null ? e.getCause().getMessage() : "unknown error");
            }
            throw new RuntimeException("Failed to call SOAP service: " + errorMsg, e);
        }
    }

    @GET
    @Path("/distance/sum")
    public Response getDistanceSum() {
        try {
            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><getDistanceSum xmlns=\"http://routeservice.example.com/soap\"/></soap:Body></soap:Envelope>";
            String response = this.callSoapService(soapBody);
            JAXBContext jaxbContext = JAXBContext.newInstance(DistanceSumResponse.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            int startIdx = response.indexOf("<distanceSumResponse");
            int endIdx = response.lastIndexOf("</distanceSumResponse>");
            if (startIdx >= 0 && endIdx > startIdx) {
                int contentStart = response.indexOf(">", startIdx) + 1;
                String content = response.substring(contentStart, endIdx).trim();
                if (content != null && !content.isEmpty()) {
                    String responseXml = "<DistanceSumResponse>" + content + "</DistanceSumResponse>";
                    DistanceSumResponse result = (DistanceSumResponse) unmarshaller.unmarshal(new StringReader(responseXml));
                    return Response.ok(result).build();
                } else {
                    throw new RuntimeException("Empty content in SOAP response. Full response: " + response.substring(0, Math.min(500, response.length())));
                }
            } else {
                throw new RuntimeException("Invalid SOAP response. Response: " + response.substring(0, Math.min(500, response.length())));
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = e.getClass().getName() + ": " + (e.getCause() != null ? e.getCause().getMessage() : "unknown error");
            }
            throw new RuntimeException("Failed to call SOAP service: " + errorMsg, e);
        }
    }

    @GET
    @Path("/distance/group")
    public Response groupByDistance() {
        try {
            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><groupByDistance xmlns=\"http://routeservice.example.com/soap\"/></soap:Body></soap:Envelope>";
            String response = this.callSoapService(soapBody);
            JAXBContext jaxbContext = JAXBContext.newInstance(DistanceGroupResponse.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            int startIdx = response.indexOf("<distanceGroupResponse");
            int endIdx = response.lastIndexOf("</distanceGroupResponse>") + 24;
            if (startIdx >= 0 && endIdx > startIdx) {
                String innerXml = response.substring(startIdx, endIdx);
                String content = innerXml.replaceFirst("<distanceGroupResponse[^>]*>", "").replaceFirst("</distanceGroupResponse>", "");
                String responseXml = "<DistanceGroupResponse>" + content + "</DistanceGroupResponse>";
                DistanceGroupResponse result = (DistanceGroupResponse) unmarshaller.unmarshal(new StringReader(responseXml));
                return Response.ok(result).build();
            } else {
                throw new RuntimeException("Invalid SOAP response. Response: " + response.substring(0, Math.min(500, response.length())));
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = e.getClass().getName() + ": " + (e.getCause() != null ? e.getCause().getMessage() : "unknown error");
            }
            throw new RuntimeException("Failed to call SOAP service: " + errorMsg, e);
        }
    }

    @GET
    @Path("/distance/greater-than")
    public Response getRoutesWithDistanceGreaterThan(@QueryParam("minDistance") Double minDistance) {
        try {
            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><getRoutesWithDistanceGreaterThan xmlns=\"http://routeservice.example.com/soap\"><minDistance xmlns=\"\">" + minDistance + "</minDistance></getRoutesWithDistanceGreaterThan></soap:Body></soap:Envelope>";
            String response = this.callSoapService(soapBody);
            JAXBContext jaxbContext = JAXBContext.newInstance(FilteredRoutesResponse.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            int startIdx = response.indexOf("<filteredRoutesResponse");
            int endIdx = response.lastIndexOf("</filteredRoutesResponse>");
            if (startIdx >= 0 && endIdx > startIdx) {
                int contentStart = response.indexOf(">", startIdx) + 1;
                String content = response.substring(contentStart, endIdx).trim();
                if (content != null && !content.isEmpty()) {
                    String responseXml = "<FilteredRoutesResponse>" + content + "</FilteredRoutesResponse>";
                    FilteredRoutesResponse result = (FilteredRoutesResponse) unmarshaller.unmarshal(new StringReader(responseXml));
                    return Response.ok(result).build();
                } else {
                    throw new RuntimeException("Empty content in SOAP response. Full response: " + response.substring(0, Math.min(500, response.length())));
                }
            } else {
                throw new RuntimeException("Invalid SOAP response. Response: " + response.substring(0, Math.min(500, response.length())));
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = e.getClass().getName() + ": " + (e.getCause() != null ? e.getCause().getMessage() : "unknown error");
            }
            throw new RuntimeException("Failed to call SOAP service: " + errorMsg, e);
        }
    }

    @POST
    @Path("/add/{idFrom}/{idTo}/{distance}")
    @Produces({"application/xml"})
    public Response createRouteBetweenLocations(@PathParam("idFrom") Long idFrom, @PathParam("idTo") Long idTo, @PathParam("distance") Double distance) {
        try {
            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><createRouteBetweenLocations xmlns=\"http://routeservice.example.com/soap\"><idFrom xmlns=\"\">" + idFrom + "</idFrom><idTo xmlns=\"\">" + idTo + "</idTo><distance xmlns=\"\">" + distance + "</distance></createRouteBetweenLocations></soap:Body></soap:Envelope>";
            String response = this.callSoapService(soapBody);
            JAXBContext jaxbContext = JAXBContext.newInstance(Route.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            int startIdx = response.indexOf("<route");
            int endIdx = response.lastIndexOf("</route>") + 8;
            if (startIdx >= 0 && endIdx > startIdx) {
                String routeXml = response.substring(startIdx, endIdx);
                Route created = (Route) unmarshaller.unmarshal(new StringReader(routeXml));
                URI location = this.uriInfo.getAbsolutePathBuilder().path(created.getId().toString()).build();
                return Response.created(location).entity(created).build();
            } else {
                throw new RuntimeException("Invalid SOAP response. Response: " + response.substring(0, Math.min(500, response.length())));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("<error>" + e.getMessage() + "</error>").build();
        }
    }
}
