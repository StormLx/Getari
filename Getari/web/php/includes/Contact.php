<?php

/**
 * Class to handle POST arguments from AJAX/JavaScript to send emails.
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

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

class Contact
{

    /**
     * @var String path for the configuration.
     */
    private $inifile = '/etc/smtp.ini';

    /**
     * @var array squeleton for response.
     */
    private $response = array(
        'message' => '',
        'statusCode' => 0,
        'statusText' => ''
    );

    /**
     * Ensure the referer is known.
     *
     * @param array $props SMTP properties with 'allowed_referers' key
     * @return validation of referer
     */
    private function checkReferer($props)
    {
        if (!filter_has_var(INPUT_SERVER, 'HTTP_REFERER')) {
            $this->response = array(
                'message' => "Missing HTTP header HTTP_REFERER",
                'statusCode' => 406,
                'statusText' => 'Not Acceptable'
            );
            return false;
        }
        $referer = filter_input(INPUT_SERVER, 'HTTP_REFERER');
        $referer_host = parse_url($referer, PHP_URL_HOST);
        $allowed_referers = explode(', ', $props['allowed_referers']);
        if (!in_array($referer_host, $allowed_referers)) {
            $this->response = array(
                'message' => "Unknown referer '$referer_host'",
                'statusCode' => 403,
                'statusText' => 'Forbidden'
            );
            return false;
        }
        return true;
    }

    /**
     * Get user input in the contact form.
     * @return array arguments typed in the form.
     */
    private function getFormArguments()
    {
        $args = filter_input_array(
            INPUT_POST,
            array(
            'from_givenname' => null,
            'from_familyname' => null,
            'from_email' => array(
                'filter' => FILTER_VALIDATE_EMAIL
                ),
                'to' => null,
                'subject' => null,
                'message' => null
                )
        );
        if ($args === null) {
            $this->response = array(
                'message' => 'Only POST request is handled.',
                'statusCode' => 400,
                'statusText' => 'Bad Request'
            );
            return null;
        }
        $missing = array();
        foreach ($args as $key => $val) {
            if ($val == null || empty($val)) {
                $missing[] = $key;
            }
        }

        if (!empty($missing)) {
            header("HTTP/1.0 400 Bad Request");
            $this->response = array(
                'message' => 'POST arguments missing: ' . implode(', ', $missing),
                'statusCode' => 400,
                'statusText' => 'Bad Request'
            );
            return null;
        }
        return $args;
    }

    /**
     * Get SMTP configuration for recipient.
     *
     * @param string $to recipient
     * @return array configuration or null if error
     */
    private function getSmtpConfiguration($to)
    {
        if (!file_exists($this->inifile)) {
            $this->response = array(
                'message' => "SMTP Configuration does not exist.",
                'statusCode' => 500,
                'statusText' => 'Internal Server Error'
            );
            return null;
        }
        $props = parse_ini_file($this->inifile, true);
        if (!in_array($to, array_keys($props))) {
            $$this->response = array(
                'message' => "{$to} is not declared in SMTP configuration.",
                'statusCode' => 500,
                'statusText' => 'Internal Server Error'
            );
            return null;
        }
        return $props[$to];
    }

    /**
     * Process POST arguments, checks and send email.
     */
    public function handle()
    {
        $args = $this->getFormArguments();
        if ($args == null) {
            $this->respond();
            return;
        }
        $props = $this->getSmtpConfiguration($args['to']);
        if ($props == null) {
            $this->respond();
            return;
        }
        if (!$this->checkReferer($props)) {
            $this->respond();
            return;
        }
        $this->response = $this->sendEmail($args, $props);
        $this->respond();
    }

    /**
     * Set headers and print JSON.
     */
    private function respond()
    {
        header('Content-Type: application/json;charset=utf-8');
        switch ($this->response['statusCode']) {
            case 200:
                header("HTTP/1.0 200 OK");
                break;
            case 400:
                header("HTTP/1.0 400 Bad Request");
                break;
            case 403:
                header("HTTP/1.0 403 Forbidden");
                break;
            case 406:
                header("HTTP/1.0 406 Not Acceptable");
                break;
            case 500:
                header("HTTP/1.0 500 Internal Server Error");
                break;
        }
        echo json_encode($this->response);
    }

    /**
     * Send email when all checks were done.
     *
     * @param  array $args  string array with mail details
     * @param  array $props SMTP configuration: username, password, host, port, secure
     * @return array response to user
     */
    private function sendEmail($args, $props)
    {
        $to = $props['recipient_email'];
        $mail = new PHPMailer(true);
        try {
            //Server settings
            $mail->SMTPDebug = 0;
            $mail->isSMTP();
            $mail->Host = $props['host'];
            $mail->SMTPAuth = $props['auth'];
            $mail->Username = $props['username'];
            $mail->Password = $props['password'];
            $mail->SMTPSecure = $props['secure'];
            $mail->Port = $props['port'];
            //Recipients
            $mail->setFrom($props['sender_email'], $props['sender_name']);
            foreach (explode(',', $to) as $email) {
                $mail->addAddress($email);
            }
            $mail->addReplyTo($args['from_email'], $args['from_givenname'] . ' ' . $args['from_familyname']);
            //Content
            $mail->isHTML(false);
            $mail->CharSet = 'UTF-8';
            $mail->Subject = $args['subject'];
            $mail->Body = $args['message'] . "\n-- \n" . sprintf($props['signature'], $to);

            $mail->send();
        } catch (Exception $e) {
            $response = array(
                'message' => "Échec de l'envoi du message « {$args['message']} ». " . $mail->ErrorInfo . '<br/>' . $e,
                'statusCode' => 500,
                'statusText' => 'Internal Server Error'
            );
            return $response;
        }

        $response = array(
            'message' => "Votre message « {$args['message']} » a été envoyé.",
            'statusCode' => 200,
            'statusText' => 'OK'
        );
        return $response;
    }
}
