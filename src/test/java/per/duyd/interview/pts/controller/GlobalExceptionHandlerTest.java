package per.duyd.interview.pts.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import per.duyd.interview.pts.dto.PurchaseTransactionErrorResponse;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

  @Test
  void shouldRethrowResponseStatusException() {
    //Given
    ResponseStatusException responseStatusException = mock(ResponseStatusException.class);

    //When
    ResponseStatusException actualException = assertThrows(ResponseStatusException.class,
        () -> globalExceptionHandler.handleOtherExceptions(responseStatusException));

    //Then
    assertThat(actualException).isEqualTo(responseStatusException);
  }

  @Test
  void shouldHandleNonFieldErrorsForBadRequest() {
    //Given
    MethodArgumentNotValidException methodArgumentNotValidException =
        mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = mock(BindingResult.class);
    when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
    ObjectError objectError = mock(ObjectError.class);
    when(objectError.getObjectName()).thenReturn("test-object-error");
    when(objectError.getDefaultMessage()).thenReturn("test-error-message");
    when(bindingResult.getAllErrors()).thenReturn(List.of(objectError));

    //When
    ResponseEntity<PurchaseTransactionErrorResponse> responseEntity =
        globalExceptionHandler.handleBadRequest(methodArgumentNotValidException);

    //Then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(responseEntity.getBody().getErrorMessage()).isEqualTo("test-object-error "
        + "test-error-message");
  }

  @Test
  void shouldReturn500InternalServerResponseForUnknownException() throws Exception {
    //Given
    RuntimeException unknownException = new RuntimeException("Unknown error");

    //When
    ResponseEntity<PurchaseTransactionErrorResponse> responseEntity =
        globalExceptionHandler.handleOtherExceptions(unknownException);

    //Then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(responseEntity.getBody().getErrorMessage()).isEqualTo("Unknown error");
  }
}