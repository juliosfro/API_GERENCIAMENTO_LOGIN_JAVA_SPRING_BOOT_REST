package curso.api.rest.exception;

import java.util.Date;

public class ErrorDetails {
        private Date timestamp;
        private String message;
        private String details;
        private String code;

        public ErrorDetails() {

        }

        public ErrorDetails(Date timestamp, String message, String details, String code) {
            super();
            this.timestamp = timestamp;
            this.message = message;
            this.details = details;
            this.code = code;
        }

        public Date getTimestamp() {
            return timestamp;
        }
        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public String getDetails() {
            return details;
        }
        public void setDetails(String details) {
            this.details = details;
        }
        public String getCode() {
            return code;
    }
        public void setCode(String code) {
            this.code = code;
    }
}
