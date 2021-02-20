/* Copyright 2018-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.github.drinkjava2.gosqlgo;

/**
 * TokenSecurity should implemented by user
 * 
 * @author Yong Zhu
 */
public interface TokenSecurity {

    /**
     * According given username and password, create a token string, return null if password is not right
     * @param username 
     * @param password 
     * @return token  the created token
     */
    public String login(String username, String password); 

    /**
     * By given token and GoSqlGo methodId, check if allow execute
     * 
     * @param token
     * @param methodId
     * @return true if allow execute
     */
    public boolean allowExecute(String token, String methodId);

}
