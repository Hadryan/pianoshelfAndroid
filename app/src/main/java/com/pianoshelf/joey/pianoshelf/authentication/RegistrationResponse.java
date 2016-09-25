package com.pianoshelf.joey.pianoshelf.authentication;

/**
 * Created by joey on 25/09/16.
 * RegistrationResponse is created to avoid a login request cycle in RegistrationView
 * When a registration is successful the subscriber cannot listen to type UserInfo since
 * later registration has to prompt a login of the newly registered user, which have the same
 * subscriber listen type of UserInfo
 */

public class RegistrationResponse extends UserInfo {
}
