package com.curicfordoc.app.database;

public class AppointmentDetail {
    private String orderId;
    private String docId;
    private String paymentId;
    private String appointmentTime;
    private String appointmentDate;
    private String patientName;
    private String patientAge;
    private String patientGender;
    private String patientContact;
    private String patientEmail;
    private String patientAddress;
    private String doctorFee;
    private String patientLoginMethod;
    private String patientLoginId;
    private String status;

    public AppointmentDetail(String orderId, String docId, String paymentId, String appointmentTime, String appointmentDate, String patientName, String patientAge, String patientGender, String patientContact, String patientEmail, String patientAddress, String doctorFee, String patientLoginMethod, String patientLoginId, String status) {
        this.orderId = orderId;
        this.docId = docId;
        this.paymentId = paymentId;
        this.appointmentTime = appointmentTime;
        this.appointmentDate = appointmentDate;
        this.patientName = patientName;
        this.patientAge = patientAge;
        this.patientGender = patientGender;
        this.patientContact = patientContact;
        this.patientEmail = patientEmail;
        this.patientAddress = patientAddress;
        this.doctorFee = doctorFee;
        this.patientLoginMethod = patientLoginMethod;
        this.patientLoginId = patientLoginId;
        this.status = status;
    }

    public AppointmentDetail() {
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public String getPatientContact() {
        return patientContact;
    }

    public void setPatientContact(String patientContact) {
        this.patientContact = patientContact;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getPatientAddress() {
        return patientAddress;
    }

    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    public String getDoctorFee() {
        return doctorFee;
    }

    public void setDoctorFee(String doctorFee) {
        this.doctorFee = doctorFee;
    }

    public String getPatientLoginMethod() {
        return patientLoginMethod;
    }

    public void setPatientLoginMethod(String patientLoginMethod) {
        this.patientLoginMethod = patientLoginMethod;
    }

    public String getPatientLoginId() {
        return patientLoginId;
    }

    public void setPatientLoginId(String patientLoginId) {
        this.patientLoginId = patientLoginId;
    }
}











