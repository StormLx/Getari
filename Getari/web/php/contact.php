<?php

/**
 * PHP page used by AJAX/JavaScript to send emails from AgroMetInfo.
 *
 * The configuration file /etc/smtp.ini is needed.
 *
 * Example:
 *
 *   [agrometinfo]
 *   host = smtp.inra.fr
 *   port = 587
 *   secure = tls
 *   auth = true
 *   username = season
 *   password = 'XXXXXX'
 *   recipient_email = Olivier.Maury@inrae.fr
 *   sender_email = season@inrae.fr
 *   sender_name = 'AgroMetInfo formulaire de contact'
 *   signature = 'Courriel envoyé à %s depuis le formulaire de contact AgroMetInfo'
 *
 * $Id$
 *
 * @author    Olivier Maury <Olivier.Maury@inrae.fr>
 * @copyright 2018 INRA
 */

namespace AgroMetInfo;

error_reporting(E_ALL);
ini_set('display_errors', 1);
require __DIR__ . '/includes/PHPMailer/Exception.php';
require __DIR__ . '/includes/PHPMailer/PHPMailer.php';
require __DIR__ . '/includes/PHPMailer/SMTP.php';
require __DIR__ . '/includes/Contact.php';

// https://developer.mozilla.org/fr/docs/Web/HTTP/Headers/Access-Control-Allow-Origin
header("Access-Control-Allow-Origin: *");

$contact = new Contact();
$contact->handle();
