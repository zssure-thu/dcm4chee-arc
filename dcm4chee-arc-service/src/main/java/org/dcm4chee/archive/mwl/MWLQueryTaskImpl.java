/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at https://github.com/gunterze/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa Healthcare.
 * Portions created by the Initial Developer are Copyright (C) 2011
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.dcm4chee.archive.mwl;

import org.dcm4che.data.Attributes;
import org.dcm4che.net.Association;
import org.dcm4che.net.Status;
import org.dcm4che.net.pdu.PresentationContext;
import org.dcm4che.net.service.BasicQueryTask;
import org.dcm4che.net.service.DicomServiceException;
import org.dcm4chee.archive.common.IDWithIssuer;
import org.dcm4chee.archive.common.QueryParam;
import org.dcm4chee.archive.mwl.dao.MWLQueryService;
import org.dcm4chee.archive.util.AuditUtils;
import org.dcm4chee.archive.util.BeanLocator;

/**
 * @author Gunter Zeilinger <gunterze@gmail.com>
 * @author Michael Backhaus <michael.backhaus@agfa.com>
 */
class MWLQueryTaskImpl extends BasicQueryTask {

    private final MWLQueryService query;

    public MWLQueryTaskImpl(Association as, PresentationContext pc, Attributes rq,
            Attributes keys, IDWithIssuer[] pids, QueryParam queryParam)
                    throws Exception {
        super(as, pc, rq, keys);
        this.query = BeanLocator.lookup(MWLQueryService.class);
        try {
            query.findScheduledProcedureSteps(pids, keys, queryParam);
        } catch (Exception e) {
            query.close();
            throw e;
        }
    }

    @Override
    protected void close() {
         query.close();
         AuditUtils.logQuery(as, rq, keys);
    }

    @Override
    protected boolean hasMoreMatches() throws DicomServiceException {
        try {
            return query.hasMoreMatches();
        }  catch (Exception e) {
            throw wrapException(Status.UnableToProcess, e);
        }
    }

    @Override
    protected Attributes nextMatch() throws DicomServiceException {
        try {
            return query.nextMatch();
        }  catch (Exception e) {
            throw wrapException(Status.UnableToProcess, e);
        }
    }

    @Override
    protected boolean optionalKeyNotSupported(Attributes match) {
        return query.optionalKeyNotSupported();
    }
}
