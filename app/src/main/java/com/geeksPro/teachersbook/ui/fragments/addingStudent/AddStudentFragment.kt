package com.geeksPro.teachersbook.ui.fragments.addingStudent

import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.core.base.BaseFragment
import com.geeksPro.teachersbook.core.di.Listeners
import com.geeksPro.teachersbook.data.local.models.LaboratoryModel
import com.geeksPro.teachersbook.data.local.models.PracticalModel
import com.geeksPro.teachersbook.data.local.models.SeminarModel
import com.geeksPro.teachersbook.data.local.models.StudentModel
import com.geeksPro.teachersbook.data.local.pref.PreferenceHelper
import com.geeksPro.teachersbook.databinding.AlertDailogToAssignGradesBinding
import com.geeksPro.teachersbook.databinding.AlertDialogAddStudentBinding
import com.geeksPro.teachersbook.databinding.AlertDialogChooseAStudentBinding
import com.geeksPro.teachersbook.databinding.AlertDialogStudentAddedBinding
import com.geeksPro.teachersbook.databinding.FragmentAddStudentBinding
import com.geeksPro.teachersbook.ui.fragments.addingStudent.adapter.ChooseAStudentAdapter
import com.geeksPro.teachersbook.ui.fragments.addingStudent.adapter.StudentAdapter
import com.geeksPro.teachersbook.ui.fragments.addingSubject.SubjectsFragment.Companion.LABORATORY
import com.geeksPro.teachersbook.ui.fragments.addingSubject.SubjectsFragment.Companion.LECTURE
import com.geeksPro.teachersbook.ui.fragments.addingSubject.SubjectsFragment.Companion.PRACTICAL
import com.geeksPro.teachersbook.ui.fragments.addingSubject.SubjectsFragment.Companion.SEMINAR
import com.geeksPro.teachersbook.ui.fragments.customdateppicker.CustomDatePickerFragment
import com.geeksPro.teachersbook.ui.fragments.customdateppicker.CustomDatePickerFragment.Companion.LOCAL_RU
import com.geeksPro.teachersbook.ui.fragments.customdateppicker.DatePickerViewModel
import com.geeksPro.teachersbook.ui.fragments.groupstatistic.GroupStatisticFragment
import com.geeksPro.teachersbook.ui.fragments.studentReportsCard.StudentReportsCardFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class AddStudentFragment :
    BaseFragment<FragmentAddStudentBinding, AddStudentViewModel>(R.layout.fragment_add_student) {

    override val viewModel: AddStudentViewModel by lazy {
        ViewModelProvider(requireActivity())[AddStudentViewModel::class.java]
    }

    private val dateViewModel: DatePickerViewModel by lazy {
        ViewModelProvider(requireActivity())[DatePickerViewModel::class.java]
    }

    private val adapterChooseStudent: ChooseAStudentAdapter by lazy {
        ChooseAStudentAdapter(this::showAlertDialogToAssignGrades)
    }
    private val adapter: StudentAdapter =
        StudentAdapter(this::onStudentClick, this::showSomethingWentWrong)

    private var listRB: List<RadioButton> = listOf()
    private var checkMode = true
    private var groupId: Long? = null
    private var groupName: String = ""
    private var selectedTypeClasses = 0
    private var date = ""
    private var typeClasses = 0

    private lateinit var preferenceHelper: PreferenceHelper

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAddStudentBinding.inflate(inflater, container, false)

    override fun sizeListener() {
        preferenceHelper = PreferenceHelper(requireContext())
        if (preferenceHelper.getSize() == "big" || Listeners.booleanLiveData.value == true) {
            Listeners.booleanLiveData.value = true
            val configuration = Configuration(resources.configuration)
            val newValue = 1f

            configuration.fontScale = newValue
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
    }

    override fun initUI() {
        super.initUI()
        binding.recyclerView.adapter = adapter
        initRadioButton()
    }

    override fun initLiveData() {
        super.initLiveData()
        getBundle()

        viewModel.subject.observe(viewLifecycleOwner) { subjectName ->
            binding.tvNameSubject.text = subjectName!!.nameSubject
        }

        dateViewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            binding.tvCalendar.text = date
            this.date = date
            updateData()
            adapter.setDate(date)
        }

        viewModel.getGroup(groupId!!).observe(viewLifecycleOwner) { groupModel ->
            if (groupModel != null) {
                groupName = groupModel.nameGroup
                binding.tvNameGroup.text = groupModel.nameGroup
                viewModel.setGroup(groupModel.id!!)
            }
        }

        viewModel.students.observe(viewLifecycleOwner) { students ->
            adapter.addStudentsList(students)
            adapterChooseStudent.addStudentsList(students)
        }

        viewModel.lecturesByGroupIdAndDate.observe(viewLifecycleOwner) {
            adapter.setLectures(it)
        }

        viewModel.laboratoriesByGroupIdAndDate.observe(viewLifecycleOwner) {
            adapter.setLaboratories(it)
        }

        viewModel.practicalsByGroupIdAndDate.observe(viewLifecycleOwner) {
            adapter.setPracticals(it)
        }

        viewModel.seminarsByGroupIdAndDate.observe(viewLifecycleOwner) {
            adapter.setSeminars(it)
        }
        viewModel.hasStudents.observe(viewLifecycleOwner) { hasStudents ->
            if (hasStudents) {
                with(binding) {
                    ivAddStudent.visibility = View.GONE
                    tvEmptyMessege.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE

                    icCheck.background.setTint(colorBlue())
                    tvMark.setTextColor(colorBlue())
                    btnStatistics.background.setTint(colorBlue())
                    btnEdit.background.setTint(colorBlue())

                    btnCheck.setOnClickListener { initBtnCheck() }

                    btnStatistics.setOnClickListener {
                        val bundle = Bundle().apply {
                            putLong(GroupStatisticFragment.GROUP_ID_KEY, groupId!!)
                            putString(GroupStatisticFragment.GROUP_NAME_KEY, groupName)
                            putInt(GroupStatisticFragment.SUBJECT_TYPE_CLASSES_KEY, typeClasses)
                        }
                        findNavController().navigate(R.id.groupStatisticFragment, bundle)
                    }

                    btnEdit.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putLong(GROUP_ID_KEY, groupId!!)
                        findNavController().navigate(
                            R.id.action_addStudentFragment_to_editingStudentFragment,
                            bundle
                        )
                    }
                }

                if (selectedTypeClasses == LECTURE) deactivationBtnGrades()
                else activationBtnGrades()

            } else {
                with(binding) {
                    ivAddStudent.visibility = View.VISIBLE
                    tvEmptyMessege.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE

                    icCheck.background.setTint(colorGray())
                    tvMark.setTextColor(colorGray())
                    btnStatistics.background.setTint(colorGray())
                    btnEdit.background.setTint(colorGray())

                    btnCheck.setOnClickListener {}

                    btnStatistics.setOnClickListener {}

                    btnEdit.setOnClickListener {}
                }
                deactivationBtnGrades()
            }
        }
    }

    override fun initClick() {
        super.initClick()
        with(binding) {

            btnBack.setOnClickListener {
                findNavController().navigate(R.id.action_addStudentFragment_to_addGroupsFragment)
            }

            btnAddStudent.setOnClickListener {
                showAlertDialogAddStudent()
            }

            btnCalendar.setOnClickListener {
                val dialogFragment = CustomDatePickerFragment()
                dialogFragment.show(parentFragmentManager, "customCalendarDialog")
            }

        }
    }

    private fun getBundle() {
        if (arguments != null) {
            val subjectId = arguments?.getLong(SUBJECT_ID_KEY)
            if (subjectId != null) {
                viewModel.getSubjectById(subjectId)
            }
            groupId = arguments?.getLong(GROUP_ID_KEY)
            if (groupId != null) {
                viewModel.setGroup(groupId!!)
                viewModel.loadStudents(groupId!!)
            }
        } else {
            groupId = viewModel.groupId.value
        }
    }

    private fun initRadioButton() {
        viewModel.subject.observe(viewLifecycleOwner) { subjectModel ->
            typeClasses = subjectModel.typeClasses
            with(binding) {
                listRB = listOf(
                    rbChooseTypeClassesLeft,
                    rbChooseTypeClassesCenter,
                    rbChooseTypeClassesRight
                )
                listRB.forEach {
                    it.visibility = View.GONE
                }
            }

            when (contTypes(subjectModel.typeClasses)) {
                1 -> initOne(subjectModel.typeClasses)
                2 -> initTwo(subjectModel.typeClasses)
                3 -> initThree(subjectModel.typeClasses)
                else -> showSomethingWentWrong()
            }
        }
    }

    private fun initThree(typeClasses: Int) {
        binding.rbChooseTypeClassesLeft.visibility = View.VISIBLE
        binding.rbChooseTypeClassesCenter.visibility = View.VISIBLE
        binding.rbChooseTypeClassesRight.visibility = View.VISIBLE
        binding.rbChooseTypeClassesLeft.isClickable = true
        when (typeClasses) {
            LECTURE + LABORATORY + PRACTICAL -> {
                initLecture(binding.rbChooseTypeClassesLeft)
                initLaboratory(binding.rbChooseTypeClassesCenter)
                initPractical(binding.rbChooseTypeClassesRight)
                adapter.setSelectedClasses(LECTURE)
            }

            LECTURE + LABORATORY + SEMINAR -> {
                initLecture(binding.rbChooseTypeClassesLeft)
                initLaboratory(binding.rbChooseTypeClassesCenter)
                initSeminary(binding.rbChooseTypeClassesRight)
                adapter.setSelectedClasses(LECTURE)
            }

            LECTURE + SEMINAR + PRACTICAL -> {
                initLecture(binding.rbChooseTypeClassesLeft)
                initPractical(binding.rbChooseTypeClassesCenter)
                initSeminary(binding.rbChooseTypeClassesRight)
                adapter.setSelectedClasses(LECTURE)
            }

            LABORATORY + PRACTICAL + SEMINAR -> {
                activationBtnGrades()
                initLaboratory(binding.rbChooseTypeClassesLeft)
                initPractical(binding.rbChooseTypeClassesCenter)
                initSeminary(binding.rbChooseTypeClassesRight)
                adapter.setSelectedClasses(LABORATORY)
            }

            else -> showSomethingWentWrong()
        }
    }

    private fun initTwo(typeClasses: Int) {
        binding.rbChooseTypeClassesLeft.visibility = View.VISIBLE
        binding.rbChooseTypeClassesRight.visibility = View.VISIBLE
        binding.rbChooseTypeClassesLeft.isClickable = true
        when (typeClasses) {
            LECTURE + LABORATORY -> {
                initLecture(binding.rbChooseTypeClassesLeft)
                initLaboratory(binding.rbChooseTypeClassesRight)
                adapter.setSelectedClasses(LECTURE)
            }

            LECTURE + PRACTICAL -> {
                initLecture(binding.rbChooseTypeClassesLeft)
                initPractical(binding.rbChooseTypeClassesRight)
                adapter.setSelectedClasses(LECTURE)
            }

            LECTURE + SEMINAR -> {
                initLecture(binding.rbChooseTypeClassesLeft)
                initSeminary(binding.rbChooseTypeClassesRight)
                adapter.setSelectedClasses(LECTURE)
            }

            LABORATORY + PRACTICAL -> {
                activationBtnGrades()
                initLaboratory(binding.rbChooseTypeClassesLeft)
                initPractical(binding.rbChooseTypeClassesRight)
                adapter.setSelectedClasses(LABORATORY)
            }

            LABORATORY + SEMINAR -> {
                activationBtnGrades()
                initLaboratory(binding.rbChooseTypeClassesLeft)
                initSeminary(binding.rbChooseTypeClassesRight)
                adapter.setSelectedClasses(LABORATORY)
            }

            PRACTICAL + SEMINAR -> {
                activationBtnGrades()
                initPractical(binding.rbChooseTypeClassesLeft)
                initSeminary(binding.rbChooseTypeClassesRight)
                adapter.setSelectedClasses(PRACTICAL)
            }

            else -> showSomethingWentWrong()
        }
    }

    private fun initOne(typeClasses: Int) {
        val radioButton = binding.rbChooseTypeClassesLeft
        radioButton.visibility = View.VISIBLE
        radioButton.isClickable = false
        when (typeClasses) {
            LECTURE -> {
                radioButton.text = getString(R.string.lecture)
                selectedTypeClasses = LECTURE
                deactivationBtnGrades()
                adapter.setSelectedClasses(LECTURE)
                binding.btnCheck.setOnClickListener {
                    initBtnCheck()
                    viewModel.getLecturesByGroupIdAndDate(groupId!!, date)
                }
            }

            LABORATORY -> {
                radioButton.text = getString(R.string.laboratory)
                selectedTypeClasses = LABORATORY
                activationBtnGrades()
                adapter.setSelectedClasses(LABORATORY)
                binding.btnCheck.setOnClickListener {
                    initBtnCheck()
                    viewModel.getLaboratoriesByGroupIdAndDate(groupId!!, date)
                }
            }

            PRACTICAL -> {
                radioButton.text = getString(R.string.practical)
                selectedTypeClasses = PRACTICAL
                activationBtnGrades()
                adapter.setSelectedClasses(PRACTICAL)
                binding.btnCheck.setOnClickListener {
                    initBtnCheck()
                    viewModel.getPracticalsByGroupIdAndDate(groupId!!, date)
                }
            }

            SEMINAR -> {
                radioButton.text = getString(R.string.seminar)
                selectedTypeClasses = SEMINAR
                activationBtnGrades()
                adapter.setSelectedClasses(SEMINAR)
                binding.btnCheck.setOnClickListener {
                    initBtnCheck()
                    viewModel.getSeminarsByGroupIdAndDate(groupId!!, date)
                }
            }

            else -> showSomethingWentWrong()
        }
    }

    private fun initSeminary(radioButton: RadioButton) {
        if (radioButton == binding.rbChooseTypeClassesLeft) {
            adapter.setSelectedClasses(SEMINAR)
            selectedTypeClasses = SEMINAR
            activationBtnGrades()
        }
        radioButton.text = getString(R.string.seminar)
        radioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                listRB.forEach {
                    if (it != radioButton) {
                        it.isChecked = false
                    }
                }
                selectedTypeClasses = SEMINAR
                activationBtnGrades()
                adapter.setSelectedClasses(SEMINAR)
                viewModel.getSeminarsByGroupIdAndDate(groupId!!, date)
            }
        }
    }

    private fun initPractical(radioButton: RadioButton) {
        if (radioButton == binding.rbChooseTypeClassesLeft) {
            adapter.setSelectedClasses(PRACTICAL)
            selectedTypeClasses = PRACTICAL
            activationBtnGrades()
        }
        radioButton.text = getString(R.string.practical)
        radioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                adapter.setSelectedClasses(PRACTICAL)
                listRB.forEach {
                    if (it != radioButton) {
                        it.isChecked = false
                    }
                }
                selectedTypeClasses = PRACTICAL
                activationBtnGrades()
                viewModel.getPracticalsByGroupIdAndDate(groupId!!, date)
            }
        }
    }

    private fun initLaboratory(radioButton: RadioButton) {
        if (radioButton == binding.rbChooseTypeClassesLeft) {
            adapter.setSelectedClasses(LABORATORY)
            selectedTypeClasses = LABORATORY
            activationBtnGrades()
        }
        radioButton.text = getString(R.string.laboratory)
        radioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                adapter.setSelectedClasses(LABORATORY)
                listRB.forEach {
                    if (it != radioButton) {
                        it.isChecked = false
                    }
                }
                selectedTypeClasses = LABORATORY
                activationBtnGrades()
                viewModel.getLaboratoriesByGroupIdAndDate(groupId!!, date)
            }
        }
    }

    private fun initLecture(radioButton: RadioButton) {
        if (radioButton == binding.rbChooseTypeClassesLeft) {
            selectedTypeClasses = LECTURE
            adapter.setSelectedClasses(LECTURE)
            deactivationBtnGrades()
        }
        radioButton.text = getString(R.string.lecture)
        radioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                adapter.setSelectedClasses(LECTURE)
                listRB.forEach {
                    if (it != radioButton) {
                        it.isChecked = false
                    }
                }
                selectedTypeClasses = LECTURE
                deactivationBtnGrades()
                viewModel.getLecturesByGroupIdAndDate(groupId!!, date)
            }
        }
    }

    private fun activationBtnGrades() {
        viewModel.hasStudents.observe(viewLifecycleOwner) {
            if (it) {
                if (selectedTypeClasses != LECTURE) {
                    with(binding) {
                        btnGrades.setOnClickListener {
                            showAlertDialogChooseAStudent()
                        }
                        icGrades.background.setTint(colorBlue())
                        tvGrades.setTextColor(colorBlue())
                    }
                }
            }
        }
    }

    private fun deactivationBtnGrades() {
        with(binding) {
            btnGrades.setOnClickListener { }
            icGrades.background.setTint(colorGray())
            tvGrades.setTextColor(colorGray())
        }
    }

    private fun contTypes(typeClasses: Int): Int {
        var count = 0
        typeClasses.toString().forEach {
            if (it.toString() == "1") {
                count++
            }
        }
        return count
    }

    private fun onStudentClick(studentModel: StudentModel) {
        val bundle = Bundle().apply {
            putLong(StudentReportsCardFragment.STUDENT_ID, studentModel.id ?: -1)
            putString(StudentReportsCardFragment.STUDENT_NAME, studentModel.name)
            putString(StudentReportsCardFragment.STUDENT_SURNAME, studentModel.surname)
            putString(StudentReportsCardFragment.GROUP_NAME, groupName)
            putInt(StudentReportsCardFragment.SUBJECT_TYPE_CLASSES, typeClasses)
            studentModel.id?.let { putLong("studentId", it) }
            putLong("groupId", groupId!!)
        }
        findNavController().navigate(R.id.studentReportsCardFragment, bundle)
    }

    private fun showAlertDialogAddStudent() {
        val addStudentBindingAD = AlertDialogAddStudentBinding.inflate(layoutInflater)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(addStudentBindingAD.root)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_background)


        addStudentBindingAD.btnBack.setOnClickListener {
            alertDialog.dismiss()
        }
        setupCapitalizationListener(addStudentBindingAD.etName)
        setupCapitalizationListener(addStudentBindingAD.etSurname)
        addStudentBindingAD.btnAddStudent.setOnClickListener {

            if (addStudentBindingAD.etName.text.toString().trim().isNotEmpty() &&
                addStudentBindingAD.etSurname.text.trim().isNotEmpty()
            ) {
                viewModel.addStudent(
                    student = StudentModel(
                        name = addStudentBindingAD.etName.text.toString().trim(),
                        surname = addStudentBindingAD.etSurname.text.toString().trim(),
                        groupId = groupId
                    )
                )

                lifecycleScope.launch {
                    delay(50)
                    viewModel.loadStudents(groupId!!)
                }
                alertDialog.dismiss()
                showAlertDialogStudentAdded()
            } else showToast(getString(R.string.tv_if_empty))
        }

        alertDialog.show()
    }

    private fun setupCapitalizationListener(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (!charSequence.isNullOrEmpty() && Character.isLowerCase(charSequence[0])) {
                    val capitalizedText =
                        charSequence.toString().replaceFirstChar { it.uppercase() }
                    editText.removeTextChangedListener(this)
                    editText.text.replace(
                        0,
                        editText.length(),
                        capitalizedText,
                        0,
                        capitalizedText.length
                    )
                    editText.addTextChangedListener(this)
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun showAlertDialogStudentAdded() {
        val studentAddedBindingAD = AlertDialogStudentAddedBinding.inflate(layoutInflater)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(studentAddedBindingAD.root)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_background)

        with(studentAddedBindingAD) {
            btnClose.setOnClickListener {
                alertDialog.dismiss()
            }

            btnAddAnotherStudent.setOnClickListener {
                alertDialog.dismiss()
                showAlertDialogAddStudent()
            }
        }

        alertDialog.show()
    }

    private fun showAlertDialogChooseAStudent() {
        val chooseAStudentBindingAD = AlertDialogChooseAStudentBinding.inflate(layoutInflater)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(chooseAStudentBindingAD.root)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_background)

        with(chooseAStudentBindingAD) {

            recyclerViewChooseAStudent.adapter = adapterChooseStudent
            adapterChooseStudent.setAlertDialog(alertDialog)

            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }

            btnChoose.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        alertDialogBuilder.setView(chooseAStudentBindingAD.root)
        alertDialog.show()
        lifecycleScope.launch {
            delay(10)
            adapterChooseStudent.notifyDataSetChanged()
        }
    }

    private fun showAlertDialogToAssignGrades(student: StudentModel) {
        var grades = 0

        val toAssignGradesBindingAD = AlertDailogToAssignGradesBinding.inflate(layoutInflater)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(toAssignGradesBindingAD.root)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_background)

        with(toAssignGradesBindingAD) {

            val radioButtonList: List<RadioButton> = listOf(
                radioButton1, radioButton2, radioButton3, radioButton4, radioButton5,
                radioButton6, radioButton7, radioButton8, radioButton9, radioButton10
            )

            for (radioButton in radioButtonList) {
                radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        grades = buttonView.text.toString().toInt()
                        for (aRadioButton in radioButtonList) {
                            if (aRadioButton.id != buttonView.id) {
                                aRadioButton.isChecked = false
                            }
                        }
                    }
                }
            }

            tvNameStudent.text = "${student.name} ${student.surname}"

            btnBack.setOnClickListener {
                alertDialog.dismiss()
            }

            btnSaveGrades.setOnClickListener {

                if (grades != 0) {
                    when (selectedTypeClasses) {
                        LABORATORY -> {
                            lifecycleScope.launch {
                                viewModel.insertLaboratories(adapter.getLaboratories())
                                viewModel.insertLaboratory(
                                    LaboratoryModel(
                                        studentId = student.id!!,
                                        groupId = groupId!!,
                                        date = date,
                                        grades = grades,
                                        visits = true,
                                    )
                                )
                                viewModel.getLaboratoriesByGroupIdAndDate(groupId!!, date)
                            }
                            showToast("${student.name} ${getString(R.string.laboratory_lessons_with_dots)} $grades")
                        }

                        PRACTICAL -> {
                            lifecycleScope.launch {
                                viewModel.insertPracticals(adapter.getPracticals())
                                viewModel.insertPractical(
                                    PracticalModel(
                                        studentId = student.id!!,
                                        groupId = groupId!!,
                                        date = date,
                                        grades = grades,
                                        visits = true
                                    )
                                )
                                viewModel.getPracticalsByGroupIdAndDate(groupId!!, date)
                            }
                            showToast("${student.name} ${getString(R.string.tv_practical_with_two_dots)} $grades")
                        }

                        SEMINAR -> {
                            lifecycleScope.launch {
                                viewModel.insertSeminars(adapter.getSeminars())
                                viewModel.insertSeminar(
                                    SeminarModel(
                                        studentId = student.id!!,
                                        groupId = groupId!!,
                                        date = date,
                                        grades = grades,
                                        visits = true
                                    )
                                )
                                viewModel.getSeminarsByGroupIdAndDate(groupId!!, date)
                            }
                            showToast("${student.name} ${getString(R.string.seminar_lessons)} $grades")
                        }
                    }
                    updateData()
                    alertDialog.dismiss()
                } else showToast(getString(R.string.select_a_grades_to_give))
            }
        }
        alertDialog.show()
    }

    private fun initBtnCheck() {
        with(binding) {
            if (checkMode) {
                icCheck.visibility = View.GONE
                icCheckMark.visibility = View.VISIBLE
                checkMode = false
            } else {
                icCheck.visibility = View.VISIBLE
                icCheckMark.visibility = View.GONE
                checkMode = true
                when (selectedTypeClasses) {
                    LECTURE -> {
                        lifecycleScope.launch {
                            viewModel.insertLectures(adapter.getLectures())
                            viewModel.getLecturesByGroupIdAndDate(groupId!!, date)
                        }
                    }

                    LABORATORY -> {
                        lifecycleScope.launch {
                            viewModel.insertLaboratories(adapter.getLaboratories())
                            viewModel.getLaboratoriesByGroupIdAndDate(groupId!!, date)
                        }
                    }

                    PRACTICAL -> {
                        lifecycleScope.launch {
                            viewModel.insertPracticals(adapter.getPracticals())
                            viewModel.getPracticalsByGroupIdAndDate(groupId!!, date)
                        }
                    }

                    SEMINAR -> {
                        lifecycleScope.launch {
                            viewModel.insertSeminars(adapter.getSeminars())
                            viewModel.getSeminarsByGroupIdAndDate(groupId!!, date)
                        }
                    }

                    else -> showSomethingWentWrong()
                }
                showToast(getString(R.string.saved))
            }
        }
        adapter.checkMode()
    }

    private fun colorGray() = ContextCompat.getColor(requireContext(), R.color.gray)
    private fun colorBlue() = ContextCompat.getColor(requireContext(), R.color.blue)

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    private fun showSomethingWentWrong() {
        showToast(getString(R.string.something_went_wrong))
    }

    private fun updateData() {
        viewModel.getLecturesByGroupIdAndDate(groupId!!, date)
        viewModel.getLaboratoriesByGroupIdAndDate(groupId!!, date)
        viewModel.getPracticalsByGroupIdAndDate(groupId!!, date)
        viewModel.getSeminarsByGroupIdAndDate(groupId!!, date)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadStudents(groupId!!)
        adapter.onResume()
        checkMode = true
        binding.icCheck.visibility = View.VISIBLE
        binding.icCheckMark.visibility = View.GONE
        if (date == "") {
            date = LocalDate.now()
                .format(DateTimeFormatter.ofPattern(CustomDatePickerFragment.DATE_FORMAT_PATTERN,
                    Locale(LOCAL_RU)
                ))
            adapter.setDate(date)
        }

        binding.tvCalendar.text = date

        for (i in listRB.indices) {
            listRB[i].isChecked = listRB[i].isChecked
        }

        updateData()
    }

    companion object {
        const val SUBJECT_ID_KEY = "Subject id key"
        const val GROUP_ID_KEY = "Group id key"
        const val TAG = "kiber"
    }
}