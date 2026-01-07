package com.example.routeservice.proxy;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/routes")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
@ApplicationScoped
public class RouteProxyResource {

    private String soapServiceUrl;
    private org.apache.http.impl.client.CloseableHttpClient httpClient;

    @Context
    private UriInfo uriInfo;

    @PostConstruct
    public void init() {
        soapServiceUrl = System.getenv("SOAP_SERVICE_URL");
        if (soapServiceUrl == null || soapServiceUrl.isEmpty()) {
            soapServiceUrl = "https://localhost:38443/route-web/RouteSoapService";
        }
        
        try {
            javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                }
            };
            javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            
            org.apache.http.conn.ssl.SSLConnectionSocketFactory sslSocketFactory = 
                new org.apache.http.conn.ssl.SSLConnectionSocketFactory(sc, (hostname, session) -> true);
            
            this.httpClient = org.apache.http.impl.client.HttpClients.custom()
                .setSSLSocketFactory(sslSocketFactory)
                .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize HTTP client: " + e.getMessage(), e);
        }
    }

    private String callSoapService(String soapBody) throws Exception {
        org.apache.http.client.methods.HttpPost httpPost = new org.apache.http.client.methods.HttpPost(soapServiceUrl);
        httpPost.setHeader("Content-Type", "text/xml; charset=utf-8");
        httpPost.setHeader("SOAPAction", "");
        httpPost.setEntity(new org.apache.http.entity.StringEntity(soapBody, "UTF-8"));
        
        org.apache.http.HttpResponse httpResponse = httpClient.execute(httpPost);
        return org.apache.http.util.EntityUtils.toString(httpResponse.getEntity());
    }

    private RouteListResponse callSoapGetRoutes(Integer page, Integer size, String[] sort, Map<String, String> filterParams) {
        try {
            String soapBody = buildGetRoutesSoapRequest(page, size, sort, filterParams);
            String response = callSoapService(soapBody);
            return parseGetRoutesResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to call SOAP getRoutes: " + e.getMessage(), e);
        }
    }

    private String buildGetRoutesSoapRequest(Integer page, Integer size, String[] sort, Map<String, String> filterParams) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:soap12=\"http://schemas.xmlsoap.org/wsdl/soap12/\">");
        sb.append("<soap:Body>");
        sb.append("<getRoutes xmlns=\"http://routeservice.example.com/soap\">");
        if (page != null) sb.append("<page>").append(page).append("</page>");
        if (size != null) sb.append("<size>").append(size).append("</size>");
        if (sort != null) {
            for (String s : sort) {
                sb.append("<sort>").append(s).append("</sort>");
            }
        }
        if (filterParams != null) {
            sb.append("<filterParams>");
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

    private RouteListResponse parseGetRoutesResponse(String xml) {
        try {
            jakarta.xml.bind.JAXBContext jaxbContext = jakarta.xml.bind.JAXBContext.newInstance(RouteListResponse.class);
            jakarta.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            int startIdx = xml.indexOf("<routeListResponse");
            int endIdx = xml.lastIndexOf("</routeListResponse>") + 20;
            if (startIdx >= 0 && endIdx > startIdx) {
                String responseXml = xml.substring(startIdx, endIdx);
                return (RouteListResponse) unmarshaller.unmarshal(new java.io.StringReader(responseXml));
            }
            return new RouteListResponse();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse SOAP response: " + e.getMessage(), e);
        }
    }

    @GET
    public Response getRoutes(
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("size") @DefaultValue("20") Integer size,
            @QueryParam("sort") String[] sort) {

        Map<String, String> filterParams = uriInfo.getQueryParameters().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("filter."))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get(0)
                ));

        RouteListResponse response = callSoapGetRoutes(page, size, sort, filterParams);
        return Response.ok(response).build();
    }

    @POST
    public Response createRoute(RouteCreateRequest request) {
        try {
            jakarta.xml.bind.JAXBContext jaxbContext = jakarta.xml.bind.JAXBContext.newInstance(RouteCreateRequest.class, Route.class);
            jakarta.xml.bind.Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(jakarta.xml.bind.Marshaller.JAXB_FRAGMENT, true);
            java.io.StringWriter sw = new java.io.StringWriter();
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

            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<soap:Body><createRoute xmlns=\"http://routeservice.example.com/soap\">" +
                "<request xmlns=\"\">" + requestXml + "</request>" +
                "</createRoute></soap:Body></soap:Envelope>";

            String response = callSoapService(soapBody);

            jakarta.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            int startIdx = response.indexOf("<route");
            int endIdx = response.lastIndexOf("</route>") + 8;
            if (startIdx >= 0 && endIdx > startIdx) {
                String routeXml = response.substring(startIdx, endIdx);
                Route createdRoute = (Route) unmarshaller.unmarshal(new java.io.StringReader(routeXml));

                URI location = uriInfo.getAbsolutePathBuilder()
                        .path(createdRoute.getId().toString())
                        .build();

                return Response.created(location)
                        .entity(createdRoute)
                        .build();
            }
            throw new RuntimeException("Invalid SOAP response. Response: " + response.substring(0, Math.min(500, response.length())));
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
            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<soap:Body><getRouteById xmlns=\"http://routeservice.example.com/soap\">" +
                "<id xmlns=\"\">" + id + "</id>" +
                "</getRouteById></soap:Body></soap:Envelope>";

            String response = callSoapService(soapBody);

            jakarta.xml.bind.JAXBContext jaxbContext = jakarta.xml.bind.JAXBContext.newInstance(Route.class);
            jakarta.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            int startIdx = response.indexOf("<route");
            int endIdx = response.lastIndexOf("</route>") + 8;
            if (startIdx >= 0 && endIdx > startIdx) {
                String routeXml = response.substring(startIdx, endIdx);
                Route route = (Route) unmarshaller.unmarshal(new java.io.StringReader(routeXml));
                return Response.ok(route).build();
            }
            throw new RuntimeException("Invalid SOAP response. Response: " + response.substring(0, Math.min(500, response.length())));
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
            jakarta.xml.bind.JAXBContext jaxbContext = jakarta.xml.bind.JAXBContext.newInstance(RouteUpdateRequest.class, Route.class);
            jakarta.xml.bind.Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(jakarta.xml.bind.Marshaller.JAXB_FRAGMENT, true);
            java.io.StringWriter sw = new java.io.StringWriter();
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

            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<soap:Body><updateRoute xmlns=\"http://routeservice.example.com/soap\">" +
                "<id xmlns=\"\">" + id + "</id>" +
                "<request xmlns=\"\">" + requestXml.replaceAll("xmlns=\"http://routeservice.example.com/soap\"", "xmlns=\"\"").replaceAll("xmlns='http://routeservice.example.com/soap'", "xmlns=''") + "</request>" +
                "</updateRoute></soap:Body></soap:Envelope>";

            String response = callSoapService(soapBody);

            jakarta.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            int startIdx = response.indexOf("<route");
            int endIdx = response.lastIndexOf("</route>") + 8;
            if (startIdx >= 0 && endIdx > startIdx) {
                String routeXml = response.substring(startIdx, endIdx);
                Route updatedRoute = (Route) unmarshaller.unmarshal(new java.io.StringReader(routeXml));
                return Response.ok(updatedRoute).build();
            }
            throw new RuntimeException("Invalid SOAP response. Response: " + response.substring(0, Math.min(500, response.length())));
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
            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<soap:Body><deleteRoute xmlns=\"http://routeservice.example.com/soap\">" +
                "<id xmlns=\"\">" + id + "</id>" +
                "</deleteRoute></soap:Body></soap:Envelope>";

            callSoapService(soapBody);

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
            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<soap:Body><getDistanceSum xmlns=\"http://routeservice.example.com/soap\"/></soap:Body></soap:Envelope>";

            String response = callSoapService(soapBody);

            jakarta.xml.bind.JAXBContext jaxbContext = jakarta.xml.bind.JAXBContext.newInstance(DistanceSumResponse.class);
            jakarta.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            int startIdx = response.indexOf("<distanceSumResponse");
            int endIdx = response.lastIndexOf("</distanceSumResponse>");
            if (startIdx >= 0 && endIdx > startIdx) {
                int contentStart = response.indexOf(">", startIdx) + 1;
                String content = response.substring(contentStart, endIdx).trim();
                if (content == null || content.isEmpty()) {
                    throw new RuntimeException("Empty content in SOAP response. Full response: " + response.substring(0, Math.min(500, response.length())));
                }
                String responseXml = "<DistanceSumResponse>" + content + "</DistanceSumResponse>";
                DistanceSumResponse result = (DistanceSumResponse) unmarshaller.unmarshal(new java.io.StringReader(responseXml));
                return Response.ok(result).build();
            }
            throw new RuntimeException("Invalid SOAP response. Response: " + response.substring(0, Math.min(500, response.length())));
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
            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<soap:Body><groupByDistance xmlns=\"http://routeservice.example.com/soap\"/></soap:Body></soap:Envelope>";

            String response = callSoapService(soapBody);

            jakarta.xml.bind.JAXBContext jaxbContext = jakarta.xml.bind.JAXBContext.newInstance(DistanceGroupResponse.class);
            jakarta.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            int startIdx = response.indexOf("<distanceGroupResponse");
            int endIdx = response.lastIndexOf("</distanceGroupResponse>") + 24;
            if (startIdx >= 0 && endIdx > startIdx) {
                String innerXml = response.substring(startIdx, endIdx);
                String content = innerXml.replaceFirst("<distanceGroupResponse[^>]*>", "").replaceFirst("</distanceGroupResponse>", "");
                String responseXml = "<DistanceGroupResponse>" + content + "</DistanceGroupResponse>";
                DistanceGroupResponse result = (DistanceGroupResponse) unmarshaller.unmarshal(new java.io.StringReader(responseXml));
                return Response.ok(result).build();
            }
            throw new RuntimeException("Invalid SOAP response. Response: " + response.substring(0, Math.min(500, response.length())));
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
            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<soap:Body><getRoutesWithDistanceGreaterThan xmlns=\"http://routeservice.example.com/soap\">" +
                "<minDistance xmlns=\"\">" + minDistance + "</minDistance>" +
                "</getRoutesWithDistanceGreaterThan></soap:Body></soap:Envelope>";

            String response = callSoapService(soapBody);

            jakarta.xml.bind.JAXBContext jaxbContext = jakarta.xml.bind.JAXBContext.newInstance(FilteredRoutesResponse.class);
            jakarta.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            int startIdx = response.indexOf("<filteredRoutesResponse");
            int endIdx = response.lastIndexOf("</filteredRoutesResponse>");
            if (startIdx >= 0 && endIdx > startIdx) {
                int contentStart = response.indexOf(">", startIdx) + 1;
                String content = response.substring(contentStart, endIdx).trim();
                if (content == null || content.isEmpty()) {
                    throw new RuntimeException("Empty content in SOAP response. Full response: " + response.substring(0, Math.min(500, response.length())));
                }
                String responseXml = "<FilteredRoutesResponse>" + content + "</FilteredRoutesResponse>";
                FilteredRoutesResponse result = (FilteredRoutesResponse) unmarshaller.unmarshal(new java.io.StringReader(responseXml));
                return Response.ok(result).build();
            }
            throw new RuntimeException("Invalid SOAP response. Response: " + response.substring(0, Math.min(500, response.length())));
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
    @Produces(MediaType.APPLICATION_XML)
    public Response createRouteBetweenLocations(
            @PathParam("idFrom") Long idFrom,
            @PathParam("idTo") Long idTo,
            @PathParam("distance") Double distance) {

        try {
            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<soap:Body><createRouteBetweenLocations xmlns=\"http://routeservice.example.com/soap\">" +
                "<idFrom xmlns=\"\">" + idFrom + "</idFrom>" +
                "<idTo xmlns=\"\">" + idTo + "</idTo>" +
                "<distance xmlns=\"\">" + distance + "</distance>" +
                "</createRouteBetweenLocations></soap:Body></soap:Envelope>";

            String response = callSoapService(soapBody);

            jakarta.xml.bind.JAXBContext jaxbContext = jakarta.xml.bind.JAXBContext.newInstance(Route.class);
            jakarta.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            int startIdx = response.indexOf("<route");
            int endIdx = response.lastIndexOf("</route>") + 8;
            if (startIdx >= 0 && endIdx > startIdx) {
                String routeXml = response.substring(startIdx, endIdx);
                Route created = (Route) unmarshaller.unmarshal(new java.io.StringReader(routeXml));

                URI location = uriInfo.getAbsolutePathBuilder()
                        .path(created.getId().toString())
                        .build();

                return Response.created(location).entity(created).build();
            }
            throw new RuntimeException("Invalid SOAP response. Response: " + response.substring(0, Math.min(500, response.length())));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("<error>" + e.getMessage() + "</error>")
                    .build();
        }
    }
}
