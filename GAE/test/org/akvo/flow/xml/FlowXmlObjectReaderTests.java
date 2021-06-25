/*
 *  Copyright (C) 2019,2021 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.akvo.flow.xml;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;

import static org.junit.jupiter.api.Assertions.*;

class FlowXmlObjectReaderTests {

    private final String MINIMAL_XML_FORM = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<survey name=\"Foo\" defaultLanguageCode=\"en\" version='1.0' app=\"akvoflowsandbox\" "
            + "surveyGroupId=\"12345\" surveyGroupName=\"Bar\" surveyId=\"67890\">"
            + "<questionGroup><heading>The Only Group</heading>"
            + "<question order=\"1\" type=\"free\" mandatory=\"true\" localeNameFlag=\"false\" id=\"24680\">"
            + "<altText type=\"translation\" language=\"sv\">Den enda frågan</altText>"
            + "<text>The Only Question</text>"
            + "</question></questionGroup></survey>";

    private final String GROUPLESS_XML_FORM = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<survey name=\"Foo\" defaultLanguageCode=\"en\" version='1.0' app=\"akvoflowsandbox\" "
            + "surveyGroupId=\"12345\" surveyGroupName=\"Bar\" surveyId=\"67890\">"
            + "</survey>";

    private final String QUESTIONLESS_XML_FORM = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<survey name=\"Foo\" defaultLanguageCode=\"en\" version='1.0' app=\"akvoflowsandbox\" "
            + "surveyGroupId=\"12345\" surveyGroupName=\"Bar\" surveyId=\"67890\">"
            + "<questionGroup><heading>The Empty Group</heading>"
            + "</questionGroup></survey>";

    private final String COMPATIBLE_XML_FORM = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<survey name=\"Foo\" defaultLanguageCode=\"en\" version='1.0' app=\"akvoflowsandbox\" "
            + "surveyGroupId=\"12345\" surveyGroupName=\"Bar\" surveyId=\"67890\">"
            + "<altText type=\"translation\" language=\"sv\">Formuläret</altText>"
            + "<questionGroup><heading>The Only Group</heading>"
            + "<altText type=\"translation\" language=\"sv\">Den enda gruppen</altText>"
            + "<question order=\"1\" type=\"free\" mandatory=\"true\" localeNameFlag=\"false\" id=\"24680\">"
            + "<altText type=\"translation\" language=\"sv\">Den enda frågan</altText>"
            + "<text>The Only Question</text>"
            + "</question></questionGroup></survey>";

    //Everything but the kitchen sink
    private final String BIG_XML_FORM = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<survey name=\"Malang VERIF Tahap 2\" defaultLanguageCode=\"en\" version='10.0' app=\"akvoflowsandbox\" surveyGroupId=\"20923121\" surveyGroupName=\"Malang VERIF Tahap 2\" surveyId=\"42842453\">"
            + "<questionGroup><heading>A. Data Responden</heading>"
            + "<question order=\"1\" type=\"free\" mandatory=\"false\" localeNameFlag=\"false\" id=\"9823003\"><text>New question - please change name</text></question>"
            + "<question order=\"2\" type=\"free\" mandatory=\"true\" localeNameFlag=\"true\" id=\"41244679\"><text>Nomor ID (Sesuai List Survei Teknis) - 12 angka (versi konsultan sebelumnya)</text><validationRule minVal=\"9.9999999999E10\" allowDecimal=\"false\" validationType=\"numeric\" signed=\"false\"/></question>"
            + "<question order=\"3\" type=\"free\" mandatory=\"true\" localeNameFlag=\"true\" id=\"43032194\"><text>Nama Responden (yang diwawancarai)</text></question>"
            + "<question order=\"4\" type=\"option\" mandatory=\"true\" localeNameFlag=\"false\" id=\"41352197\"><options allowOther=\"false\" allowMultiple=\"false\" renderType=\"radio\"><option value=\"Pria\"><text>Pria</text></option><option value=\"Wanita\"><text>Wanita</text></option></options><text>Jenis Kelamin</text></question>"
            + "<question order=\"5\" type=\"option\" mandatory=\"true\" localeNameFlag=\"false\" id=\"43022227\"><options allowOther=\"false\" allowMultiple=\"false\" renderType=\"radio\"><option value=\"Pemilik Rumah\"><text>Pemilik Rumah</text></option><option value=\"Keluarga\"><text>Keluarga</text></option><option value=\"Penyewa\"><text>Penyewa</text></option><option value=\"Lainnya\"><text>Lainnya</text></option></options><text>Hubungan dengan KK</text></question>"
            + "<question order=\"6\" type=\"free\" mandatory=\"true\" localeNameFlag=\"false\" id=\"43092140\"><text>Alamat</text></question>"
            + "<question order=\"7\" type=\"free\" mandatory=\"true\" localeNameFlag=\"false\" id=\"42842454\"><text>RT</text><validationRule allowDecimal=\"false\" validationType=\"numeric\" signed=\"false\"/></question>"
            + "<question order=\"8\" type=\"free\" mandatory=\"true\" localeNameFlag=\"false\" id=\"43092141\"><text>RW</text><validationRule allowDecimal=\"false\" validationType=\"numeric\" signed=\"false\"/></question>"
            + "<question order=\"9\" type=\"free\" mandatory=\"true\" localeNameFlag=\"false\" id=\"43032195\"><text>Kelurahan/Desa</text></question>"
            + "<question order=\"10\" type=\"free\" mandatory=\"true\" localeNameFlag=\"false\" id=\"43062160\"><text>Kecamatan</text></question>"
            + "<question order=\"11\" type=\"free\" mandatory=\"true\" localeNameFlag=\"false\" id=\"41352199\"><text>Kabupaten/Kota</text></question>"
            + "<question order=\"12\" type=\"free\" mandatory=\"true\" localeNameFlag=\"true\" id=\"43002174\"><text>Propinsi</text></question>"
            + "<question order=\"13\" type=\"option\" mandatory=\"true\" localeNameFlag=\"false\" id=\"42542365\"><options allowOther=\"false\" allowMultiple=\"false\" renderType=\"radio\"><option value=\"Tidak ada sambungan\"><text>Tidak ada sambungan</text></option><option value=\"450 Watt\"><text>450 Watt</text></option><option value=\"900 Watt\"><text>900 Watt</text></option><option value=\"1300 Watt\"><text>1300 Watt</text></option><option value=\"Diatas 1300 Watt\"><text>Diatas 1300 Watt</text></option></options><text>Daya Listrik</text></question>"
            + "<question order=\"14\" type=\"free\" mandatory=\"true\" localeNameFlag=\"false\" id=\"43002175\"><text>ID Pelanggan PDAM</text></question>"
            + "<question order=\"15\" type=\"geo\" mandatory=\"true\" localeNameFlag=\"false\" id=\"42542366\"><text>Koordinat GPS</text></question></questionGroup>"
            + "<questionGroup><heading>B. Suplai Air &amp; Pemasangan Sambungan Rumah</heading>"
            + "<question order=\"1\" type=\"date\" mandatory=\"true\" localeNameFlag=\"false\" id=\"43062161\"><text>Tanggal berita acara pemasangan Sambungan Rumah </text></question>"
            + "<question order=\"2\" type=\"option\" mandatory=\"true\" localeNameFlag=\"false\" id=\"43052195\"><options allowOther=\"false\" allowMultiple=\"false\" renderType=\"radio\"><option value=\"Ya \"><text>Ya </text></option><option value=\"Tidak\"><text>Tidak</text></option></options><text>Memiliki bukti pembayaran tagihan air untuk 2 bulan terakhir</text></question>"
            + "<question order=\"3\" type=\"option\" mandatory=\"true\" localeNameFlag=\"false\" id=\"43072221\"><options allowOther=\"false\" allowMultiple=\"false\" renderType=\"radio\"><option value=\"Ya \"><text>Ya </text></option><option value=\"Tidak\"><text>Tidak</text></option></options><text>Apakah pemasangan sambungan baru sudah sesuai dengan spesifikasi teknis?</text></question>"
            + "<question order=\"4\" type=\"option\" mandatory=\"true\" localeNameFlag=\"false\" id=\"43072222\"><options allowOther=\"false\" allowMultiple=\"false\" renderType=\"radio\"><option value=\"Ya \"><text>Ya </text></option><option value=\"Tidak\"><text>Tidak</text></option></options><text>Apakah kran/gate valve terpasang?</text></question>"
            + "<question order=\"5\" type=\"option\" mandatory=\"true\" localeNameFlag=\"false\" id=\"41244683\"><options allowOther=\"false\" allowMultiple=\"false\" renderType=\"radio\"><option value=\"Ya \"><text>Ya </text></option><option value=\"Tidak\"><text>Tidak</text></option></options><text>Meter air terpasang?</text></question>"
            + "<question order=\"6\" type=\"option\" mandatory=\"true\" localeNameFlag=\"false\" id=\"43062164\"><options allowOther=\"false\" allowMultiple=\"false\" renderType=\"radio\"><option value=\"Ya\"><text>Ya</text></option><option value=\"Tidak\"><text>Tidak</text></option></options><text>Apakah ada kran sebelum meter air/Plug Valve? </text></question>"
            + "<question order=\"7\" type=\"option\" mandatory=\"true\" localeNameFlag=\"false\" id=\"43062165\"><dependency answer-value=\"Ya \" question=\"43072221\"/><options allowOther=\"false\" allowMultiple=\"false\" renderType=\"radio\"><option value=\"Ya\"><text>Ya</text></option><option value=\"Tidak\"><text>Tidak</text></option></options><text>Apakah meter air sesuai dengan SNI?</text></question>"
            + "<question order=\"8\" type=\"option\" mandatory=\"true\" localeNameFlag=\"false\" id=\"43012186\"><options allowOther=\"false\" allowMultiple=\"false\" renderType=\"radio\"><option value=\"Kurang dari 12 jam\"><text>Kurang dari 12 jam</text></option><option value=\"Lebih atau sama dengan 12 jam\"><text>Lebih atau sama dengan 12 jam</text></option><option value=\"24 jam\"><text>24 jam</text></option></options><text>Berapa jam air mengalir dalam sehari?</text></question>"
            + "<question order=\"9\" type=\"option\" mandatory=\"true\" localeNameFlag=\"false\" id=\"43002176\"><options allowOther=\"false\" allowMultiple=\"false\" renderType=\"radio\"><option value=\"Baik\"><text>Baik</text></option><option value=\"Sedang\"><text>Sedang</text></option><option value=\"Kurang\"><text>Kurang</text></option></options><text>Bagaimana kuantitas, kualitas dan tekanan air?</text></question>"
            + "<question order=\"10\" type=\"option\" mandatory=\"true\" localeNameFlag=\"false\" id=\"43062166\"><options allowOther=\"false\" allowMultiple=\"false\" renderType=\"radio\"><option value=\"Ya \"><text>Ya </text></option><option value=\"Tidak\"><text>Tidak</text></option></options><text>Apakah anda puas dengan pemasangan SR</text></question>"
            + "</questionGroup>"
            + "<questionGroup><heading>C. Foto</heading>"
            + "<question order=\"1\" type=\"photo\" mandatory=\"true\" localeNameFlag=\"false\" id=\"43092142\"><text>Foto Rumah</text></question>"
            + "<question order=\"2\" type=\"photo\" mandatory=\"true\" localeNameFlag=\"false\" id=\"43032199\"><text>Foto Meter dan air mengalir dari kran  </text></question>"
            + "<question order=\"3\" type=\"photo\" mandatory=\"true\" localeNameFlag=\"false\" id=\"43062167\"><text>Rekening air 2 bulan</text></question></questionGroup>"
            + "<questionGroup><heading>D. Hasil Verifikasi</heading>"
            + "<question order=\"1\" type=\"option\" mandatory=\"false\" localeNameFlag=\"false\" id=\"43042143\"><options allowOther=\"false\" allowMultiple=\"false\" renderType=\"radio\"><option value=\"Memenuhi syarat\"><text>Memenuhi syarat</text></option><option value=\"Belum\"><text>Belum</text></option></options><text>Hasil Verifikasi</text></question>"
            + "<question order=\"2\" type=\"option\" mandatory=\"true\" localeNameFlag=\"false\" id=\"42842457\"><dependency answer-value=\"Belum\" question=\"43042143\"/><options allowOther=\"false\" allowMultiple=\"false\" renderType=\"radio\"><option value=\"Pemasangan meter air tidak sesuai spesifikasi\"><text>Pemasangan meter air tidak sesuai spesifikasi</text></option><option value=\"Meter air tidak menggunakan SNI\"><text>Meter air tidak menggunakan SNI</text></option><option value=\"Air tidak mengalir\"><text>Air tidak mengalir</text></option><option value=\"Tidak memiliki bukti rekening 2 bulan terakhir \"><text>Tidak memiliki bukti rekening 2 bulan terakhir </text></option></options><text>Alasan belum</text></question>"
            + "</questionGroup>"
            + "</survey>";

    @Test
    void testParseValidForm() throws IOException {

        SurveyDto testFormDto = PublishedForm.parse(MINIMAL_XML_FORM, true).toDto(); //be strict
        assertNotNull(testFormDto);

        assertEquals("Foo", testFormDto.getName());
        assertEquals("1.0", testFormDto.getVersion());
        assertNotNull(testFormDto.getQuestionGroupList());
        assertEquals(1, testFormDto.getQuestionGroupList().size());

        QuestionGroupDto qg = testFormDto.getQuestionGroupList().get(0);
        assertEquals(1, qg.getOrder());
        assertEquals("The Only Group", qg.getName());
        assertNotNull(qg.getQuestionList());
        assertEquals(1, qg.getQuestionList().size());

        QuestionDto q = qg.getQuestionList().get(0);
        assertNotNull(q);
        assertEquals("The Only Question", q.getText());
        assertEquals(QuestionType.FREE_TEXT, q.getType());
        assertTrue(q.getMandatoryFlag());
        assertFalse(q.getLocaleNameFlag());
        assertEquals(24680, q.getKeyId());
        assertNotNull(q.getTranslationMap());
        assertNull(q.getTranslationMap().get("fr")); //Should NOT be a French translation
        assertNotNull(q.getTranslationMap().get("sv")); //Should be a Swedish translation
        assertEquals("Den enda frågan", q.getTranslationMap().get("sv").getText());
    }

    @Test
    void testParseGrouplessForm() throws IOException {

        SurveyDto testFormDto = PublishedForm.parse(GROUPLESS_XML_FORM, true).toDto(); //be strict
        assertNotNull(testFormDto);

        assertEquals("Foo", testFormDto.getName());
        assertEquals("1.0", testFormDto.getVersion());
        assertNull(testFormDto.getQuestionGroupList());
    }

    @Test
    void testParseQuestionlessForm() throws IOException {

        SurveyDto testFormDto = PublishedForm.parse(QUESTIONLESS_XML_FORM, true).toDto(); //be strict
        assertNotNull(testFormDto);

        assertEquals("Foo", testFormDto.getName());
        assertEquals("1.0", testFormDto.getVersion());
        assertNotNull(testFormDto.getQuestionGroupList());
        assertEquals(1, testFormDto.getQuestionGroupList().size());

        QuestionGroupDto qg = testFormDto.getQuestionGroupList().get(0);
        assertEquals(1, qg.getOrder());
        assertEquals("The Empty Group", qg.getName());
        assertNull(qg.getQuestionList());
    }

    @Test
    void testParseBigForm() throws IOException {

        XmlForm testForm = PublishedForm.parse(BIG_XML_FORM, false); //be strict?
        assertNotNull(testForm);

        SurveyDto testFormDto = testForm.toDto();
        assertNotNull(testFormDto);

        assertNotNull(testFormDto.getQuestionGroupList());
        assertEquals(4, testFormDto.getQuestionGroupList().size());

        QuestionGroupDto qg1 = testFormDto.getQuestionGroupList().get(0);
        assertEquals(1, qg1.getOrder());

        QuestionGroupDto qg2 = testFormDto.getQuestionGroupList().get(1);
        assertEquals(2, qg2.getOrder());

        QuestionGroupDto qg3 = testFormDto.getQuestionGroupList().get(2);
        assertEquals(3, qg3.getOrder());

        QuestionGroupDto qg4 = testFormDto.getQuestionGroupList().get(3);
        assertEquals(4, qg4.getOrder());

    }

    /*
     * This tests if parsing does not break for a possible "future" backwards-compatible form
     * with altText translations of group and form names
     */
    @Test
    void testParseCompatibleForm() throws IOException {

        SurveyDto testFormDto = PublishedForm.parse(COMPATIBLE_XML_FORM, false).toDto(); //not strict
        assertNotNull(testFormDto);

        assertEquals("Foo", testFormDto.getName());
        assertEquals("1.0", testFormDto.getVersion());
        assertNotNull(testFormDto.getQuestionGroupList());
        assertEquals(1, testFormDto.getQuestionGroupList().size());

        QuestionGroupDto qg = testFormDto.getQuestionGroupList().get(0);
        assertEquals(1, qg.getOrder());
        assertEquals("The Only Group", qg.getName());
        assertNotNull(qg.getQuestionList());
        assertEquals(1, qg.getQuestionList().size());

        QuestionDto q = qg.getQuestionList().get(0);
        assertNotNull(q);
        assertEquals("The Only Question", q.getText());
        assertEquals(QuestionType.FREE_TEXT, q.getType());
        assertTrue(q.getMandatoryFlag());
        assertFalse(q.getLocaleNameFlag());
        assertEquals(24680, q.getKeyId());
        assertNotNull(q.getTranslationMap());
        assertNull(q.getTranslationMap().get("fr")); //Should NOT be a French translation
        assertNotNull(q.getTranslationMap().get("sv")); //Should be a Swedish translation
        assertEquals("Den enda frågan", q.getTranslationMap().get("sv").getText());
    }
}
