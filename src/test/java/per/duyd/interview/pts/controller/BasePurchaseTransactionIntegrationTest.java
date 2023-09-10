package per.duyd.interview.pts.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import per.duyd.interview.pts.BaseIntegrationTest;
import per.duyd.interview.pts.dto.PurchaseTransactionResponse;

public class BasePurchaseTransactionIntegrationTest extends BaseIntegrationTest {
  @Autowired
  protected ObjectMapper objectMapper;

  public static final String PURCHASE_TRANSACTION_PATH = "/purchase-transaction";

  public PurchaseTransactionResponse postPurchaseTransaction(String requestFile)
      throws Exception {
    MvcResult mvcResult = mockMvc.perform(post(PURCHASE_TRANSACTION_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(readFileToJsonString(requestFile)))
        .andExpect(status().is(200))
        .andReturn();

    return objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
        PurchaseTransactionResponse.class);
  }
}
