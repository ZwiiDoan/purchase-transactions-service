package per.duyd.interview.pts.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static per.duyd.interview.pts.util.DateTimeUtil.UTC_ZONE_ID;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import per.duyd.interview.pts.BaseIntegrationTest;
import per.duyd.interview.pts.dto.PurchaseTransactionResponse;

public class BasePurchaseTransactionIntegrationTest extends BaseIntegrationTest {
  @Autowired
  protected ObjectMapper objectMapper;

  public static final String PURCHASE_TRANSACTION_PATH = "/v1/purchase-transaction";

  public static final LocalDate TRANSACTION_DATE = LocalDate.now(UTC_ZONE_ID);
  public static final String TRANSACTION_DATE_PLACEHOLDER = "{{transactionDate}}";

  public PurchaseTransactionResponse postPurchaseTransaction(String requestFile)
      throws Exception {
    MvcResult mvcResult = mockMvc.perform(post(PURCHASE_TRANSACTION_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(readFileToJsonString(requestFile).replace(TRANSACTION_DATE_PLACEHOLDER,
                TRANSACTION_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE))))
        .andExpect(status().is(200))
        .andReturn();

    return objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
        PurchaseTransactionResponse.class);
  }
}
