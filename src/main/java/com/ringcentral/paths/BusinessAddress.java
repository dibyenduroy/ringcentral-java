package com.ringcentral.paths;

import com.ringcentral.definitions.BusinessAddressInfo;

public class BusinessAddress {
    static
    public class GetResponse {
        // Canonical URI of the business address resource
        public String uri;
        // Company business name
        public String company;
        // Company business email address
        public String email;
        // Company business address
        public BusinessAddressInfo businessAddress;

        public GetResponse uri(String uri) {
            this.uri = uri;
            return this;
        }

        public GetResponse company(String company) {
            this.company = company;
            return this;
        }

        public GetResponse email(String email) {
            this.email = email;
            return this;
        }

        public GetResponse businessAddress(BusinessAddressInfo businessAddress) {
            this.businessAddress = businessAddress;
            return this;
        }
    }

    static
    public class PutParameters {
        // Company business name
        public String company;
        // Company business email address
        public String email;
        // Company business address
        public BusinessAddressInfo businessAddress;

        public PutParameters company(String company) {
            this.company = company;
            return this;
        }

        public PutParameters email(String email) {
            this.email = email;
            return this;
        }

        public PutParameters businessAddress(BusinessAddressInfo businessAddress) {
            this.businessAddress = businessAddress;
            return this;
        }
    }

    static
    public class PutResponse {
        // Canonical URI of the business address resource
        public String uri;
        // Company business name
        public String company;
        // Company business email address
        public String email;
        // Company business address
        public BusinessAddressInfo businessAddress;

        public PutResponse uri(String uri) {
            this.uri = uri;
            return this;
        }

        public PutResponse company(String company) {
            this.company = company;
            return this;
        }

        public PutResponse email(String email) {
            this.email = email;
            return this;
        }

        public PutResponse businessAddress(BusinessAddressInfo businessAddress) {
            this.businessAddress = businessAddress;
            return this;
        }
    }
}