package com.example.projectmobileprogramming

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val phoneDiterima = intent.getStringExtra("EXTRA_PHONE") ?: "08123456789"
        val nameDiterima = intent.getStringExtra("EXTRA_NAME") ?: "Pengguna"
        val pinDiterima = intent.getStringExtra("EXTRA_PIN") ?: "123456"

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF1F5F9)
                ) {
                    DashboardScreen(
                        initialPhone = phoneDiterima,
                        initialName = nameDiterima,
                        savedPin = pinDiterima,
                        onBackToLogin = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(
    initialPhone: String,
    initialName: String,
    savedPin: String,
    onBackToLogin: () -> Unit
) {
    var saldo by remember { mutableStateOf(500000) }
    var nominalInput by remember { mutableStateOf("") }
    var targetPhone by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("topup") } // Menu: topup, transfer, history

    // State Dialog PIN
    var showPinDialog by remember { mutableStateOf(false) }
    var inputPinTransaksi by remember { mutableStateOf("") }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    // State Fitur 1: Sembunyikan/Tampilkan Saldo (Privacy Toggle)
    var isBalanceVisible by remember { mutableStateOf(true) }

    // State Fitur 2: Pencarian / Filter Kata Kunci Riwayat (Search Query)
    var searchQuery by remember { mutableStateOf("") }
    var filterCategory by remember { mutableStateOf("Semua") }

    val transactionList = remember { mutableStateListOf(
        "Bonus Aktivasi Akun: +Rp 500.000",
        "Top-Up Saldo: +Rp 100.000",
        "Transfer ke 08112233: -Rp 50.000"
    ) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // --- HEADER KARTU PROFIL & LOGOUT ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A), shape = RoundedCornerShape(20.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Halo, $initialName ✨", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = initialPhone, fontSize = 12.sp, color = Color(0xFF94A3B8))
            }
            Button(
                onClick = onBackToLogin,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(text = "Keluar", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- KARTU SALDO UTAMA + FITUR 1: TOMBOL PRIVASI (SEBUNYIKAN SALDO) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2563EB)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Total Saldo DompetKu", fontSize = 12.sp, color = Color(0xFFDBEAFE), fontWeight = FontWeight.Medium)

                    // Tombol Mata Terbuka/Tertutup untuk Menyembunyikan Saldo
                    TextButton(
                        onClick = { isBalanceVisible = !isBalanceVisible },
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Text(
                            text = if (isBalanceVisible) "👁️ Sembunyikan" else "🔒 Tampilkan",
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isBalanceVisible) "Rp $saldo" else "Rp ••••••••••",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- TAB MENU NAVIGASI ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { selectedTab = "topup" },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (selectedTab == "topup") Color(0xFF0F172A) else Color(0xFFE2E8F0))
            ) {
                Text(text = "Top-Up", color = if (selectedTab == "topup") Color.White else Color.DarkGray, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
            Button(
                onClick = { selectedTab = "transfer" },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (selectedTab == "transfer") Color(0xFF0F172A) else Color(0xFFE2E8F0))
            ) {
                Text(text = "Transfer", color = if (selectedTab == "transfer") Color.White else Color.DarkGray, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
            Button(
                onClick = { selectedTab = "history" },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (selectedTab == "history") Color(0xFF0F172A) else Color(0xFFE2E8F0))
            ) {
                Text(text = "Riwayat", color = if (selectedTab == "history") Color.White else Color.DarkGray, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- KONTEN UTAMA ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (selectedTab == "topup") {
                    Text(text = "📥 Top-Up Saldo Instan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = nominalInput,
                        onValueChange = { nominalInput = it },
                        label = { Text("Nominal Top-Up (Cth: 50000)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val nominal = nominalInput.toIntOrNull()
                            if (nominal != null && nominal > 0) {
                                pendingAction = {
                                    saldo += nominal
                                    transactionList.add(0, "Top-Up Saldo: +Rp $nominal")
                                    Toast.makeText(context, "Top-Up Berhasil!", Toast.LENGTH_SHORT).show()
                                    nominalInput = ""
                                }
                                inputPinTransaksi = ""
                                showPinDialog = true
                            } else {
                                Toast.makeText(context, "Masukkan nominal yang valid!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text(text = "Konfirmasi Top-Up", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    }

                } else if (selectedTab == "transfer") {
                    Text(text = "📤 Transfer Saldo Aman", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = targetPhone,
                        onValueChange = { targetPhone = it },
                        label = { Text("Nomor HP Tujuan") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = nominalInput,
                        onValueChange = { nominalInput = it },
                        label = { Text("Nominal Transfer") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val nominalTransfer = nominalInput.toIntOrNull()
                            if (targetPhone.isNotEmpty() && nominalTransfer != null && nominalTransfer > 0) {
                                if (saldo >= nominalTransfer) {
                                    pendingAction = {
                                        saldo -= nominalTransfer
                                        transactionList.add(0, "Transfer ke $targetPhone: -Rp $nominalTransfer")
                                        Toast.makeText(context, "Transfer Berhasil!", Toast.LENGTH_SHORT).show()
                                        nominalInput = ""
                                        targetPhone = ""
                                    }
                                    inputPinTransaksi = ""
                                    showPinDialog = true
                                } else {
                                    Toast.makeText(context, "Saldo tidak mencukupi!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Lengkapi data dengan benar!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text(text = "Kirim Transfer Sekarang", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    }

                } else {
                    // --- HALAMAN RIWAYAT DENGAN 2 FITUR BARU (WIDGET RINGKASAN & PENCARIAN) ---
                    Text(text = "📜 Riwayat & Aktivitas", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(8.dp))

                    // --- FITUR 3: WIDGET RINGKASAN KEUANGAN OTOMATIS DI ATAS RIWAYAT ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Total Aktivitas", fontSize = 11.sp, color = Color.Gray)
                                Text(text = "${transactionList.size} Transaksi", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Status Dompet", fontSize = 11.sp, color = Color.Gray)
                                Text(text = "Aman & Aktif ✅", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- FITUR 2: KOTAK PENCARIAN KATA KUNCI RIWAYAT (SEARCH BAR) ---
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Cari riwayat (cth: Top-Up / Transfer)...") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Filter kategori tombol
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Semua", "Top-Up", "Transfer").forEach { cat ->
                            Button(
                                onClick = { filterCategory = cat },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(2.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (filterCategory == cat) Color(0xFF2563EB) else Color(0xFFF1F5F9)
                                )
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    color = if (filterCategory == cat) Color.White else Color.DarkGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Logika Filter Kategori + Search Query
                    val filteredList = transactionList.filter { item ->
                        val matchesCategory = when (filterCategory) {
                            "Top-Up" -> item.contains("Top-Up") || item.contains("Aktivasi")
                            "Transfer" -> item.contains("Transfer")
                            else -> true
                        }
                        val matchesSearch = item.contains(searchQuery, ignoreCase = true)
                        matchesCategory && matchesSearch
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredList) { item ->
                            val isIncome = item.contains("Top-Up") || item.contains("Aktivasi")
                            val badgeColor = if (isIncome) Color(0xFF10B981) else Color(0xFFEF4444)

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                            ) {
                                Text(
                                    text = item,
                                    modifier = Modifier.padding(14.dp),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = badgeColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- POPUP DIALOG KEAMANAN PIN ---
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text(text = "🔐 Verifikasi PIN Keamanan", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(text = "Masukkan PIN rahasia kamu untuk mengesahkan transaksi ini:", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inputPinTransaksi,
                        onValueChange = { inputPinTransaksi = it },
                        label = { Text("PIN Rahasia") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputPinTransaksi == savedPin) {
                            showPinDialog = false
                            pendingAction?.invoke()
                        } else {
                            Toast.makeText(context, "PIN Salah! Transaksi dibatalkan.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text(text = "Verifikasi")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text(text = "Batal", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}