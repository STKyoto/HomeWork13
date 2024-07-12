package org.example.CustomerService;

public class CreateCustomer {
    public Customer createCustomer() {
        Customer customer = new Customer();
        customer.setName("John");
        customer.setUsername("John101");
        customer.setEmail("John101@gmail.com");
        Address address = new Address();
        address.setStreet("Main");
        address.setSuite("Xoxo");
        address.setCity("Kyiv");
        address.setZipcode("101-101");
        Geo geo = new Geo();
        geo.setLat("111-222");
        geo.setLng("222-111");
        address.setGeo(geo);
        customer.setAddress(address);
        customer.setPhone("888-777-666");
        customer.setWebsite("John.com");
        Company company = new Company();
        company.setName("JonhComoany");
        company.setCatchPhrase("Mine");
        company.setBs("best company");
        customer.setCompany(company);

        return customer;
    }
}
