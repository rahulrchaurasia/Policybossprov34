package com.policyboss.policybosspro.core.oldWayApi.controller.zoho;

import android.content.Context;

import com.policyboss.policybosspro.core.model.ticketRaise.CreateTicketrequest;
import com.policyboss.policybosspro.core.oldWayApi.IResponseSubcriber;
import com.policyboss.policybosspro.core.oldWayApi.oldRequestBuilder.ZohoRequestBuilder;
import com.policyboss.policybosspro.core.response.raiseTicket.RaiseTicketWebDocResponse;
import com.policyboss.policybosspro.core.response.ticket.RaiseTicketCommentResponse;
import com.policyboss.policybosspro.core.response.ticket.TicketCategoryResponse;
import com.policyboss.policybosspro.core.response.webDocResponse.CommonWebDocResponse;
import com.policyboss.policybosspro.utils.Constant;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rajeev Ranjan on 01/03/2018.
 */

public class ZohoController implements IZoho {

    ZohoRequestBuilder.ZohoNetworkService zohoNetworkService;

    Context mContext;

    public ZohoController(Context context) {
        mContext = context;
        zohoNetworkService = new ZohoRequestBuilder().getService();

    }


    @Override
    public void getTicketCategories(IResponseSubcriber iResponseSubcriber) {

        zohoNetworkService.getTicketCategories().enqueue(new Callback<TicketCategoryResponse>() {
            @Override
            public void onResponse(Call<TicketCategoryResponse> call, Response<TicketCategoryResponse> response) {
                if (response.body() != null) {

                    //callback of data
                    if (response.body().getStatusNo() == 0) {
                        new AsyncZohoMaster(mContext, response.body().getMasterData()).execute();
                        iResponseSubcriber.OnSuccess(response.body(), response.body().getMessage());
                    } else {
                        iResponseSubcriber.OnFailure(new RuntimeException("" + response.body().getMessage()));
                    }


                } else {
                    //failure
                    iResponseSubcriber.OnFailure(new RuntimeException("Enable to reach server, Try again later"));
                }
            }

            @Override
            public void onFailure(Call<TicketCategoryResponse> call, Throwable t) {
                if (t instanceof ConnectException) {
                    iResponseSubcriber.OnFailure(t);
                } else if (t instanceof SocketTimeoutException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Check your internet connection"));
                } else if (t instanceof UnknownHostException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Check your internet connection"));
                } else if (t instanceof NumberFormatException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Unexpected server response"));
                } else {
                    iResponseSubcriber.OnFailure(new RuntimeException(t.getMessage()));
                }
            }
        });
    }

    @Override
    public void createTicket(CreateTicketrequest createTicketrequest, IResponseSubcriber iResponseSubcriber) {

    }

    @Override
    public void getListOfTickets(String fbaid, IResponseSubcriber iResponseSubcriber) {

    }

    @Override
    public void uploadDocuments(MultipartBody.Part document, HashMap<String, String> body, IResponseSubcriber iResponseSubcriber) {

    }

    @Override
    public void viewCommentOfTickets(String ticket_req_id, IResponseSubcriber iResponseSubcriber) {

    }

    @Override
    public void saveCommentOfTickets(String ticket_req_id, String comment, String docpath, String StatusID, IResponseSubcriber iResponseSubcriber) {

    }

    @Override
    public void uploadRaiseTicketDocWeb(MultipartBody.Part document, IResponseSubcriber iResponseSubcriber) {

        String url = Constant.base_url + "/mobile_raiseticket_doc";
        zohoNetworkService.uploadDocumentRaiseTicketWeb(url,document).enqueue(new Callback<RaiseTicketWebDocResponse>() {
            @Override
            public void onResponse(Call<RaiseTicketWebDocResponse> call, Response<RaiseTicketWebDocResponse> response) {
                if (response.body() != null) {
                    if (response.body().getStatusNo() == 0) {
                        //callback of data
                        iResponseSubcriber.OnSuccess(response.body(), "");
                    } else {
                        iResponseSubcriber.OnFailure(new RuntimeException(response.body().getMessage()));
                    }


                } else {
                    //failure
                    iResponseSubcriber.OnFailure(new RuntimeException("Enable to reach server, Try again later"));
                }
            }

            @Override
            public void onFailure(Call<RaiseTicketWebDocResponse> call, Throwable t) {
                if (t instanceof ConnectException) {
                    iResponseSubcriber.OnFailure(t);
                } else if (t instanceof SocketTimeoutException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Check your internet connection"));
                } else if (t instanceof UnknownHostException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Check your internet connection"));
                } else if (t instanceof NumberFormatException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Unexpected server response"));
                } else {
                    iResponseSubcriber.OnFailure(new RuntimeException(t.getMessage()));
                }
            }
        });
    }

    @Override
    public void uploadCommonDocuments(MultipartBody.Part document, HashMap<String, String> body, IResponseSubcriber iResponseSubcriber) {

        //Constant.base_url +
        String url = "https://horizon.policyboss.com:5443/postservicecall/policyboss_upload_doc";

        //   String url = "https://qa-www.policyboss.com:3443/postservicecall/policyboss_upload_doc";

        zohoNetworkService.uploadCommonDocumentWeb(url , document, body).enqueue(new Callback<CommonWebDocResponse>() {
            @Override
            public void onResponse(Call<CommonWebDocResponse> call, Response<CommonWebDocResponse> response) {
                if (response.body() != null) {
                    if (response.body().getStatus().equals("Success")) {
                        //callback of data
                        iResponseSubcriber.OnSuccess(response.body(), response.body().getMsg());
                    } else {
                        iResponseSubcriber.OnFailure(new RuntimeException(response.body().getMsg()));
                    }


                } else {
                    //failure
                    iResponseSubcriber.OnFailure(new RuntimeException("Enable to reach server, Try again later"));
                }
            }

            @Override
            public void onFailure(Call<CommonWebDocResponse> call, Throwable t) {
                if (t instanceof ConnectException) {
                    iResponseSubcriber.OnFailure(t);
                } else if (t instanceof SocketTimeoutException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Server Error"));
                } else if (t instanceof UnknownHostException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Server Errorn"));
                } else if (t instanceof NumberFormatException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Unexpected server response"));
                } else {
                    iResponseSubcriber.OnFailure(new RuntimeException(t.getMessage()));
                }
            }
        });

    }
}
