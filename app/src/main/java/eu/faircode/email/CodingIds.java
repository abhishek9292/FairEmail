package eu.faircode.email;

/*
    This file is part of FairEmail.

    FairEmail is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FairEmail is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FairEmail.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2018-2026 by Marcel Bokhorst (M66B)
*/

/**
 * Coding identify numbers (c-n): a single index of page identifiers.
 *
 * Each id names exactly one UI page. The id is appended to the page title as
 * "[c&lt;id&gt;]" (for example "Inbox [c1]" for the Inbox folder/conversations list),
 * so a page can be referenced unambiguously later.
 *
 * Index (keep in sync with usage):
 *   c1   folder messages list (Inbox / conversations)   FragmentMessages.updateState()
 *   c2   folders list                                     FragmentFolders
 *   c3   about                                            FragmentAbout
 *   c4   legend                                           FragmentLegend
 *   c5   pro / donation                                   FragmentPro
 *   c6   operations                                       FragmentOperations
 *   c7   compose                                          FragmentCompose
 *   c8   accounts list                                    FragmentAccounts
 *   c9   account edit                                     FragmentAccount / FragmentPop
 *   c10  folder edit                                      FragmentFolder
 *   c11  identities list                                  FragmentIdentities
 *   c12  identity edit                                    FragmentIdentity
 *   c13  rules list                                       FragmentRules
 *   c14  rule edit                                        FragmentRule
 *   c15  answers                                          FragmentAnswers
 *   c16  answer edit                                      FragmentAnswer
 *   c17  setup (MAIN)                                     FragmentSetup
 *   c18  options: receive (synchronize)                   FragmentOptionsSynchronize
 *   c19  options: send                                    FragmentOptionsSend
 *   c20  options: connection                              FragmentOptionsConnection
 *   c21  options: display                                 FragmentOptionsDisplay
 *   c22  options: behavior                                FragmentOptionsBehavior
 *   c23  options: privacy                                 FragmentOptionsPrivacy
 *   c24  options: encryption                              FragmentOptionsEncryption
 *   c25  options: notifications                           FragmentOptionsNotifications
 *   c26  options: integrations                            FragmentOptionsIntegrations
 *   c27  options: misc                                    FragmentOptionsMisc
 *   c28  options: backup                                  FragmentOptionsBackup
 *   c29  search results                                   FragmentMessages (SEARCH)
 *   c30  thread / message view                            FragmentMessages (THREAD)
 *   c31  quick setup (wizard)                             FragmentQuickSetup
 *   c32  setup: Gmail                                     FragmentGmail
 *   c33  setup: OAuth                                     FragmentOAuth
 *   c34  logs                                             FragmentLogs
 *   c35  welcome / EULA                                   FragmentEula
 *   c36  order / reorder                                  FragmentOrder
 *
 * To add a page: append a C_N constant below and a row above, then use it where
 * the page title is set, e.g. setCodingTitle(name, CodingIds.C2).
 */
public class CodingIds {
    // c1: folder messages list (Inbox / conversations) - FragmentMessages.updateState()
    public static final String C1 = "c1";
    // c2: folders list
    public static final String C2 = "c2";
    // c3: about
    public static final String C3 = "c3";
    // c4: legend
    public static final String C4 = "c4";
    // c5: pro / donation
    public static final String C5 = "c5";
    // c6: operations
    public static final String C6 = "c6";
    // c7: compose
    public static final String C7 = "c7";
    // c8: accounts list
    public static final String C8 = "c8";
    // c9: account edit
    public static final String C9 = "c9";
    // c10: folder edit
    public static final String C10 = "c10";
    // c11: identities list
    public static final String C11 = "c11";
    // c12: identity edit
    public static final String C12 = "c12";
    // c13: rules list
    public static final String C13 = "c13";
    // c14: rule edit
    public static final String C14 = "c14";
    // c15: answers
    public static final String C15 = "c15";
    // c16: answer edit
    public static final String C16 = "c16";
    // c17: setup (MAIN)
    public static final String C17 = "c17";
    // c18: options: receive (synchronize)
    public static final String C18 = "c18";
    // c19: options: send
    public static final String C19 = "c19";
    // c20: options: connection
    public static final String C20 = "c20";
    // c21: options: display
    public static final String C21 = "c21";
    // c22: options: behavior
    public static final String C22 = "c22";
    // c23: options: privacy
    public static final String C23 = "c23";
    // c24: options: encryption
    public static final String C24 = "c24";
    // c25: options: notifications
    public static final String C25 = "c25";
    // c26: options: integrations
    public static final String C26 = "c26";
    // c27: options: misc
    public static final String C27 = "c27";
    // c28: options: backup
    public static final String C28 = "c28";
    // c29: search results (FragmentMessages SEARCH)
    public static final String C29 = "c29";
    // c30: thread / message view (FragmentMessages THREAD)
    public static final String C30 = "c30";
    // c31: quick setup (wizard)
    public static final String C31 = "c31";
    // c32: setup: Gmail
    public static final String C32 = "c32";
    // c33: setup: OAuth
    public static final String C33 = "c33";
    // c34: logs
    public static final String C34 = "c34";
    // c35: welcome / EULA
    public static final String C35 = "c35";
    // c36: order / reorder
    public static final String C36 = "c36";
}
